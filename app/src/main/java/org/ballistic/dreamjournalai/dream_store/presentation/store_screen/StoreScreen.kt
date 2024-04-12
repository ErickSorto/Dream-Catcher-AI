package org.ballistic.dreamjournalai.dream_store.presentation.store_screen

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.dream_store.presentation.anonymous_store_screen.AnonymousStoreScreen
import org.ballistic.dreamjournalai.dream_store.presentation.store_screen.components.CustomButtonLayout
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.MainScreenEvent


//import all compose-

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoreScreen(
    storeScreenViewModelState: StoreScreenViewModelState,
    onMainEvent: (MainScreenEvent) -> Unit = {},
    onStoreEvent: (StoreEvent) -> Unit = {},
    navigateToAccountScreen: () -> Unit = {},
) {
    LaunchedEffect(Unit) {
        storeScreenViewModelState.authRepository.reloadFirebaseUser()
    }
    val isAnonymous = storeScreenViewModelState.isUserAnonymous.collectAsStateWithLifecycle().value
    val activity = LocalContext.current as Activity


    onMainEvent(MainScreenEvent.SetBottomBarState(true))
    onMainEvent(MainScreenEvent.SetFloatingActionButtonState(true))


    if (isAnonymous) {
        onMainEvent(MainScreenEvent.SetTopBarState(true))
        AnonymousStoreScreen(
            paddingValues = PaddingValues(bottom = 68.dp),
        ) {
            navigateToAccountScreen()
        }
    } else {
        onMainEvent(MainScreenEvent.SetTopBarState(false))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .align(alignment = Alignment.BottomCenter)
                    .padding(bottom = 68.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 56.dp, start = 16.dp, end = 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            colorResource(id = R.color.light_black).copy(alpha = 0.7f)
                        )
                       ,
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Get more dream tokens to do more with your dreams, and support the " +
                                "development of Dream Journal AI",
                        maxLines = 1,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.basicMarquee().padding(vertical = 12.dp, horizontal = 16.dp)
                    )
                }

                DreamBenefitInfoLayout()

                Spacer(modifier = Modifier.weight(1f))
                CustomButtonLayout(
                    storeScreenViewModelState = storeScreenViewModelState,
                    buy100IsClicked = {
                        onStoreEvent(StoreEvent.ToggleLoading(true))
                        onStoreEvent(StoreEvent.Buy100DreamTokens(activity))
                    },
                    buy500IsClicked = {
                        onStoreEvent(StoreEvent.ToggleLoading(true))
                        onStoreEvent(StoreEvent.Buy500DreamTokens(activity))
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DreamBenefitInfoLayout() {
    //one page and a little bit of the next page is visible
    val oneAndABitPerViewport = object : PageSize {
        override fun Density.calculateMainAxisPageSize(
            availableSpace: Int,
            pageSpacing: Int
        ): Int {
            return (availableSpace * .8f).toInt()
        }
    }
    val pagerState = rememberPagerState(pageCount = {
        4
    })

    HorizontalPager(
        state = pagerState,
        pageSize = oneAndABitPerViewport,
        contentPadding = PaddingValues(horizontal = 16.dp),
        pageSpacing = 16.dp,
        modifier = Modifier.padding(top = 16.dp)
    ) { page ->
        when (page) {
            0 -> DreamTokenBenefitItem(DreamTokenBenefit.DreamPainting)
            1 -> DreamTokenBenefitItem(DreamTokenBenefit.DreamDictionary)
            2 -> DreamTokenBenefitItem(DreamTokenBenefit.DreamInterpretation)
            3 -> DreamTokenBenefitItem(DreamTokenBenefit.DreamAdFree)
        }
    }
}

@Composable
fun DreamTokenBenefitItem(dreamTokenBenefit: DreamTokenBenefit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(9f / 12f)
            .clip(RoundedCornerShape(8.dp))
            .background(colorResource(id = R.color.light_black).copy(alpha = 0.8f))
    ) {
        Image(
            painter = painterResource(id = dreamTokenBenefit.image),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = dreamTokenBenefit.title,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = dreamTokenBenefit.description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
