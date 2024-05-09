package org.ballistic.dreamjournalai.dream_store.presentation.store_screen

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.components.DreamTokenLayout
import org.ballistic.dreamjournalai.core.components.dynamicBottomNavigationPadding
import org.ballistic.dreamjournalai.dream_store.presentation.anonymous_store_screen.AnonymousStoreScreen
import org.ballistic.dreamjournalai.dream_store.presentation.store_screen.components.CustomButtonLayout
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.MainScreenEvent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoreScreen(
    storeScreenViewModelState: StoreScreenViewModelState,
    bottomPaddingValue: Dp,
    onMainEvent: (MainScreenEvent) -> Unit = {},
    onStoreEvent: (StoreEvent) -> Unit = {},
    navigateToAccountScreen: () -> Unit = {},
) {
    LaunchedEffect(Unit) {
        storeScreenViewModelState.authRepository.reloadFirebaseUser()
    }
    val isAnonymous = storeScreenViewModelState.isUserAnonymous.collectAsStateWithLifecycle().value
    val tokenTotal = storeScreenViewModelState.dreamTokens.collectAsStateWithLifecycle().value
    val activity = LocalContext.current as Activity


    onMainEvent(MainScreenEvent.SetBottomBarVisibilityState(true))
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = bottomPaddingValue)
                .dynamicBottomNavigationPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 56.dp, start = 16.dp, end = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        colorResource(id = R.color.light_black).copy(alpha = 0.7f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Get more Dream Tokens",
                    maxLines = 1,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    modifier = Modifier
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                        .align(Alignment.Center)
                )
            }

            DreamBenefitInfoLayout(tokenTotal)

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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DreamBenefitInfoLayout(
    totalDreamTokens: Int = 0
) {
    val orientation = LocalContext.current.resources.configuration.orientation
    val pageAspectWidth = if (orientation == 1) .8f else .25f

    //one page and a little bit of the next page is visible
    val oneAndABitPerViewport = object : PageSize {
        override fun Density.calculateMainAxisPageSize(
            availableSpace: Int,
            pageSpacing: Int
        ): Int {
            return (availableSpace * pageAspectWidth).toInt()
        }
    }
    val pagerState = rememberPagerState(pageCount = {
        5
    })

    HorizontalPager(
        state = pagerState,
        pageSize = oneAndABitPerViewport,
        contentPadding = PaddingValues(horizontal = 16.dp),
        pageSpacing = 16.dp,
        modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)
    ) { page ->
        when (page) {
            0 -> DreamTokenBenefitItem(DreamTokenBenefit.DreamTokenSlideBenefit, totalDreamTokens)
            1 -> DreamTokenBenefitItem(DreamTokenBenefit.DreamPainting)
            2 -> DreamTokenBenefitItem(DreamTokenBenefit.DreamDictionary)
            3 -> DreamTokenBenefitItem(DreamTokenBenefit.DreamInterpretation)
            4 -> DreamTokenBenefitItem(DreamTokenBenefit.DreamAdFree)
        }
    }
}

@Composable
fun DreamTokenBenefitItem(
    dreamTokenBenefit: DreamTokenBenefit,
    totalDreamTokens: Int = 0
) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(9f / 12f)
                .clip(RoundedCornerShape(8.dp))
                .background(colorResource(id = R.color.light_black).copy(alpha = 0.8f))
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(id = dreamTokenBenefit.image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                contentScale = ContentScale.FillBounds
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = dreamTokenBenefit.title,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            if (dreamTokenBenefit.description.isNotEmpty()) {
                Text(
                    text = dreamTokenBenefit.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                for (i in 1..4) {
                    when (i) {
                        1 -> CheckAndBenefit(dreamTokenBenefit.benefit1)
                        2 -> CheckAndBenefit(dreamTokenBenefit.benefit2)
                        3 -> CheckAndBenefit(dreamTokenBenefit.benefit3)
                        4 -> CheckAndBenefit(dreamTokenBenefit.benefit4)
                    }
                }
            }
        }
        if (dreamTokenBenefit == DreamTokenBenefit.DreamTokenSlideBenefit) {
            DreamTokenLayout(
                totalDreamTokens = totalDreamTokens,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp)
            )
        }
    }

}

@Composable
fun CheckAndBenefit(benefit: String) {
    Row(
        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.check_mark),
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = benefit,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
        )
    }
}