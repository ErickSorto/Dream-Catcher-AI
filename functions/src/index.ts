/* eslint-disable eol-last */
import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
import * as corsFactory from "cors";
import * as nodemailer from "nodemailer";
import { google } from "googleapis";

admin.initializeApp();

const firestore = admin.firestore();
const cors = corsFactory({ origin: true });

// The time (in minutes) after which unverified users should be deleted
const DELETE_UNVERIFIED_USERS_AFTER = 60;

export const deleteUnverifiedUsers =
  functions.pubsub.schedule("every 60 minutes").onRun(async () => {
    const currentTime = admin.firestore.Timestamp.now();
    const cutoffTime =
      currentTime.toMillis() - DELETE_UNVERIFIED_USERS_AFTER * 60 * 1000;
    const cutoffTimestamp = admin.firestore.Timestamp.fromMillis(cutoffTime);

    const usersSnapshot = await firestore
      .collection("users")
      .where("registrationTimestamp", "<=", cutoffTimestamp)
      .get();

    const deletePromises = usersSnapshot.docs.map(async (doc) => {
      const uid = doc.id;
      const userRecord = await admin.auth().getUser(uid);

      // Skip if it's an anonymous account
      if (userRecord.providerData.length === 0) {
        return;
      }

      // Only delete if it's not an anonymous account and the email is not verified
      if (!userRecord.emailVerified) {
        await admin.auth().deleteUser(uid);
        await doc.ref.delete();
        functions.logger.info(`Deleted unverified user with UID: ${uid}.`);
      }
    });

    await Promise.all(deletePromises);

    functions.logger.info(`Processed ${usersSnapshot.size} users.`);
  });

export const deleteAnonymousInactiveUsers = functions.pubsub.schedule("every 24 hours").onRun(async () => {
    const currentTime = admin.firestore.Timestamp.now();
    // 30 days in milliseconds: 30 days * 24 hours/day * 60 minutes/hour * 60 seconds/minute * 1000 ms/second
    const cutoffTime = currentTime.toMillis() - (30 * 24 * 60 * 60 * 1000);
    const cutoffTimestamp = admin.firestore.Timestamp.fromMillis(cutoffTime);

    const usersSnapshot = await firestore
        .collection("users")
        .where("lastActiveTimestamp", "<=", cutoffTimestamp)
        .get();

    const deletePromises = usersSnapshot.docs.map(async (doc) => {
        const uid = doc.id;
        const userRecord = await admin.auth().getUser(uid);

        // Only delete if it's an anonymous account and if the user hasn't interacted in the last 30 days
        if (userRecord.providerData.length === 0) {
            await admin.auth().deleteUser(uid);
            await doc.ref.delete();
            functions.logger.info(`Deleted anonymous user with UID: ${uid} due to inactivity.`);
        }
    });

    await Promise.all(deletePromises);

    functions.logger.info(`Processed ${usersSnapshot.size} users.`);
});


exports.handleAccountLinking = functions.https.onCall(async (data, context) => {
    // Destructure and validate UIDs
    const {permanentUid, anonymousUid} = data;

    // Log starting of account linking process
    functions.logger.info(`Starting account linking. Anon UID: ${anonymousUid}, Perm UID: ${permanentUid}`);

    // Ensure that the function is called by an authenticated user
    if (!context.auth || !context.auth.uid) {
        functions.logger.error("No context.auth or context.auth.uid found. The function must be called while authenticated.");
        throw new functions.https.HttpsError("unauthenticated", "The function must be called while authenticated.");
    }

    // Ensure that the provided UIDs are valid
    if (!anonymousUid || !permanentUid || context.auth.uid !== permanentUid) {
        functions.logger.error("Invalid UIDs or insufficient permissions.");
        throw new functions.https.HttpsError("invalid-argument", "Invalid UIDs or insufficient permissions.");
    }

    try {
        const anonymousDreamsRef = firestore.collection("users").doc(anonymousUid).collection("my_dreams");
        const permanentDreamsRef = firestore.collection("users").doc(permanentUid).collection("my_dreams");
        let lastSnapshot = null;

        do {
            // Fetch anonymous user's dreams and prepare for transfer
            const anonymousDreamsSnapshot = await (lastSnapshot ? anonymousDreamsRef.startAfter(lastSnapshot.docs[lastSnapshot.docs.length - 1]).limit(500).get() : anonymousDreamsRef.limit(500).get());
            const batch = firestore.batch();

            anonymousDreamsSnapshot.forEach((doc) => {
                const newDreamRef = permanentDreamsRef.doc(doc.id);
                batch.set(newDreamRef, doc.data());
            });

            // Commit the batch transfer of dreams
            await batch.commit();
            lastSnapshot = anonymousDreamsSnapshot;
        } while (lastSnapshot && lastSnapshot.size === 500);

        // Optional: Delete the anonymous user account if needed
        // await admin.auth().deleteUser(anonymousUid);

        functions.logger.info("Account linking and dream transfer successful.");
        return {success: true, message: "All dreams transferred successfully."};
    } catch (error) {
        functions.logger.error(`Error during account linking: ${error.message}`);
        throw new functions.https.HttpsError("internal", "Error during account linking process.");
    }
});


export const handleUserCreate = functions.auth.user().onCreate(async (user) => {
    // Check if it's an anonymous account
    const isAnonymous = user.providerData.length === 0;

    // Check if the user signed in with Google
    const isGoogleSignIn = user.providerData
        .some((provider) => provider.providerId === "google.com");

    // Prepare the new user document
    const newUser = {
        uid: user.uid,
        displayName: user.displayName || "Anonymous",
        email: user.email || "",
        emailVerified: isGoogleSignIn || user.emailVerified || false,
        registrationTimestamp: admin.firestore.FieldValue.serverTimestamp(),
        lastActiveTimestamp: admin.firestore.FieldValue.serverTimestamp(),
        dreamTokens: isAnonymous ? 0 : 25, // No dreamTokens for anonymous users
    };

    // Write the user document to Firestore
    await firestore.collection("users").doc(user.uid).set(newUser);

    // Log the creation
    if (isAnonymous) {
        functions.logger.info(`Created new anonymous user with UID: ${user.uid}.`);
    } else {
        functions.logger.info(`Created new user with UID: ${user.uid} using ${isGoogleSignIn ? "Google Sign In" : "email and password"}.`);
    }
});

exports.handlePurchaseVerification = functions.https.onRequest(async (req, res) => {
  cors(req, res, async () => {
    const data = req.body.data;
    const userId = data.userId;
    const dreamTokens = data.dreamTokens;

    console.log("handlePurchaseVerification - userId:" + userId + ", dreamTokens:" + dreamTokens);

    const isPurchaseValid = true; // This should be the result of your purchase verification process

    if (isPurchaseValid) {
      // Update the user's dreamTokens in Firestore
      const userRef = firestore.collection("users").doc(userId);
      await userRef.update({
        dreamTokens: admin.firestore.FieldValue.increment(dreamTokens),
      });

      res.status(200).json({ result: { success: true } });
    } else {
      res.status(200).json({ result: { success: false } });
    }
  });
});

type FirebaseAuthError = {
    code: string;
    message: string;
};

// eslint-disable-next-line @typescript-eslint/no-explicit-any
function isFirebaseAuthError(error: any): error is FirebaseAuthError {
    return typeof error.code === "string" && typeof error.message === "string";
}

exports.createAccountAndSendEmailVerification = functions.https.onCall(async (data, context) => {
    functions.logger.info("Starting createAccount function", { structuredData: true });

    const userEmail = data.email;
    const userPassword = data.password;
    functions.logger.info("Processing for Email: ${userEmail}", { userEmail: userEmail });

    try {
        const existingUser = await admin.auth().getUserByEmail(userEmail);
        if (existingUser.emailVerified) {
            functions.logger.info("Account exists and is verified", { userEmail: userEmail });
            return { message: "Account exists already" };
        } else {
            functions.logger.info("Account exists but is not verified, sending verification email", { userEmail: userEmail });
            const verificationLink = await admin.auth().generateEmailVerificationLink(userEmail);
            await sendVerificationEmail(userEmail, verificationLink);
            return { message: "Verification email sent!" };
        }
    } catch (error) {
        if (error.code === "auth/user-not-found") {
            // User does not exist, create the user
            const userRecord = await admin.auth().createUser({
                email: userEmail,
                password: userPassword,
                emailVerified: false
            });
            functions.logger.info("User created with UID: ${userRecord.uid}", { uid: userRecord.uid, userEmail: userEmail });

            const verificationLink = await admin.auth().generateEmailVerificationLink(userEmail);
            await sendVerificationEmail(userEmail, verificationLink); // Use the extracted email sending logic
            return { message: "Verification email sent and account created! Please verify email to log in." };
        } else {
            functions.logger.error("An error occurred while creating account or sending verification email", { error: error, userEmail: userEmail });
            throw new functions.https.HttpsError("internal", "Unable to create account or send verification email");
        }
    }
});

async function sendVerificationEmail(userEmail: string, verificationLink: string) {
    const emailConfig = functions.config().email || {};
    const appEmail = emailConfig.credentials;

    const oauthConfig = functions.config().oauth || {};
    const clientID = oauthConfig.client_id;
    const clientSecret = oauthConfig.client_secret;

    const refreshTokenConfig = functions.config().refreshtoken || {};
    const refreshToken = refreshTokenConfig.token;

    const oauth2Client = new google.auth.OAuth2(
        clientID,
        clientSecret,
        "https://developers.google.com/oauthplayground"
    );

    oauth2Client.setCredentials({
        refresh_token: refreshToken
    });

    const result = await oauth2Client.getAccessToken();
    const token = result.token;

    const transporter = nodemailer.createTransport({
        host: "smtp.gmail.com",
        port: 465,
        secure: true,
        auth: {
            type: "OAuth2",
            user: appEmail,
            clientId: clientID,
            clientSecret: clientSecret,
            refreshToken: refreshToken,
            accessToken: token
        }
    } as nodemailer.TransportOptions);

    await transporter.sendMail({
        from: `"Dream Journal AI" <${appEmail}>`, // Added app name for clarity
        to: userEmail,
        subject: "Complete Your Registration with Dream Journal AI",
        text: `Welcome to Dream Journal AI!\n\nThanks for signing up. Please verify your email by clicking on this link:
        ${verificationLink}\n\nIf you did not sign up for a Dream Journal AI account, you can safely
        ignore this email.\n\nBest,\nDream Journal AI Team`,
        html: `
        <div style="font-family: Arial, sans-serif; padding: 20px;">
            <h2>Welcome to Dream Journal AI!</h2>
            <p>Thanks for signing up. Please click the button below to verify your email address and complete your registration.</p>
            <a href="${verificationLink}" style="background-color: #4CAF50; color: white;
             padding: 14px 20px; text-align: center; text-decoration: none; display: inline-block; border-radius: 4px;">Verify Email</a>
            <p>If you did not sign up for a Dream Journal AI account, you can safely ignore this email.</p>
            <p>Best,<br>Dream Journal AI Team</p>
            <hr>
            <p style="font-size: 0.8em;">If the button above doesn't work, copy and paste this link into your browser:
             <a href="${verificationLink}">${verificationLink}</a></p>
        </div>
        `
    });
}
