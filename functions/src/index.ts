/* eslint-disable eol-last */
import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
import * as corsFactory from "cors";
import * as nodemailer from "nodemailer";

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

export const deleteAnonymousUsers = functions.pubsub.schedule("every 24 hours").onRun(async () => {
    const currentTime = admin.firestore.Timestamp.now();
    const cutoffTime = currentTime.toMillis() - (1 * 1 * 60 * 60 * 1000); // 31 days in milliseconds
    const cutoffTimestamp = admin.firestore.Timestamp.fromMillis(cutoffTime);

    const usersSnapshot = await firestore
        .collection("users")
        .where("registrationTimestamp", "<=", cutoffTimestamp)
        .get();

    const deletePromises = usersSnapshot.docs.map(async (doc) => {
        const uid = doc.id;
        const userRecord = await admin.auth().getUser(uid);

        // Only delete if it's an anonymous account
        if (userRecord.providerData.length === 0) {
            await admin.auth().deleteUser(uid);
            await doc.ref.delete();
            functions.logger.info(`Deleted anonymous user with UID: ${uid}.`);
        }
    });

    await Promise.all(deletePromises);

    functions.logger.info(`Processed ${usersSnapshot.size} users.`);
});

export const handleAccountLinking = functions.https.onCall(async (data, context) => {
    const {permanentUid, anonymousUid} = data;

    functions.logger.info("Starting account linking. Anon UID: " + anonymousUid + ", Perm UID: " + permanentUid);

    if (!context.auth || !context.auth.uid) {
        functions.logger.error("No context.auth or context.auth.uid found. The function must be called while authenticated.");
        throw new functions.https.HttpsError("unauthenticated", "The function must be called while authenticated.");
    }

    if (context.auth.uid !== permanentUid) {
        functions.logger.error("Context.auth.uid (" + context.auth.uid +
        ") does not match permanent UID (" + permanentUid + "). The authenticated user does not have sufficient permissions.");
        throw new functions.https.HttpsError("permission-denied", "The authenticated user does not have sufficient permissions.");
    }

    const anonymousDreamsRef = firestore.collection("users").doc(anonymousUid).collection("my_dreams");
    const permanentDreamsRef = firestore.collection("users").doc(permanentUid).collection("my_dreams");

    let lastSnapshot = null;

    do {
        const anonymousDreamsSnapshot = await (lastSnapshot ?
            anonymousDreamsRef.startAfter(lastSnapshot.docs[lastSnapshot.docs.length - 1]).limit(500).get() :
            anonymousDreamsRef.limit(500).get());

        const batch = firestore.batch();

        // Log the number of dreams
        functions.logger.info("Anon UID " + anonymousUid + " has " + anonymousDreamsSnapshot.size + " dreams.");

        // Create new dream docs in the permanent account and keep in the anonymous account
        anonymousDreamsSnapshot.forEach((doc) => {
            const newDreamRef = permanentDreamsRef.doc(doc.id);
            functions.logger.info("Transferring dream with ID " + doc.id + " to permanent account (" + permanentUid + ").");
            batch.set(newDreamRef, doc.data());
        });

        // Commit the batch
        await batch.commit();

        functions.logger.info("Successfully committed batch");

        lastSnapshot = anonymousDreamsSnapshot;
    } while (lastSnapshot && lastSnapshot.size === 500);

    // Fetch the user record for the permanent user
    const permanentUserRecord = await admin.auth().getUser(permanentUid);

    // If the permanent user's email is verified, delete the anonymous account
    if (permanentUserRecord.emailVerified) {
        await admin.auth().deleteUser(anonymousUid);
        await firestore.collection("users").doc(anonymousUid).delete();
        functions.logger.info(`Deleted anonymous user with UID: ${anonymousUid} after transfer.`);
    }

    return {success: true, message: "All dreams transferred successfully."};
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
        dreamTokens: isAnonymous ? 0 : 50, // No dreamTokens for anonymous users
        // Add any other fields you need
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

    console.log(`handlePurchaseVerification - userId: ${userId}, dreamTokens: ${dreamTokens}`);

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

export const createAccount = functions.https.onCall(async (data, context) => {
    // Get the user's email and password from the data passed to the function
    const userEmail = data.email;
    const userPassword = data.password;

    // Create the user with the Firebase Admin SDK
    const userRecord = await admin.auth().createUser({
        email: userEmail,
        password: userPassword,
        emailVerified: false
    });

    // Generate the verification email link
    const verificationLink = await admin.auth().generateEmailVerificationLink(userEmail);

    // Get the application's email and password from Firebase environment variables
    const appEmail = functions.config().email.credentials;
    const appPassword = functions.config().password.credentials;

    // Get the OAuth2 client ID and client secret from Firebase environment variables
    const clientID = functions.config().oauth.client_id;
    const clientSecret = functions.config().oauth.client_secret;

    const oauth2Client = new google.auth.OAuth2(
        clientID, // Client ID
        clientSecret, // Client Secret
        'https://developers.google.com/oauthplayground' // Redirect URL
    );

    // Replace the following line with the method to get the refresh token
    // const refreshToken = 'YOUR_REFRESH_TOKEN';

    const { token } = await oauth2Client.getAccessToken();

    // Set up nodemailer with your SMTP details
    const transporter = nodemailer.createTransport({
        service: "gmail",
        auth: {
            type: 'OAuth2',
            user: appEmail,
            clientId: clientID,
            clientSecret: clientSecret,
            refreshToken: refreshToken,
            accessToken: token
        }
    });

    // Send the verification email
    await transporter.sendMail({
        from: appEmail,
        to: userEmail,
        subject: "Email Verification",
        text: `Please verify your email by clicking on the following link: ${verificationLink}`,
        html: `<p>Please verify your email by clicking on the following link: <a href="${verificationLink}">${verificationLink}</a></p>`
    });

    return { uid: userRecord.uid };
});

