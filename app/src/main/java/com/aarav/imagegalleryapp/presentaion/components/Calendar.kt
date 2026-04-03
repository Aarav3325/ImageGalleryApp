package com.aarav.imagegalleryapp.presentaion.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aarav.imagegalleryapp.R
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

enum class DayOfWeek(val abbr: String) {
    SUNDAY("Sun"),
    MONDAY("Mon"),
    TUESDAY("Tue"),
    WEDNESDAY("Wed"),
    THURSDAY("Thu"),
    FRIDAY("Fri"),
    SATURDAY("Sat")
}

@Composable
fun CalendarComponent(
    selectedDay: LocalDate? = null,
    onSelect: (LocalDate) -> Unit,
    imageDates: Set<LocalDate> = emptySet()
) {

//    val month = YearMonth.now()
//    Log.d("CALENDAR", month.monthValue.toString())
//    Log.d("CALENDAR", "lenhth " + month.lengthOfMonth())
//    val firstDay = month.atDay(1)
//    Log.d("CALENDAR", "day of week" + (firstDay.dayOfWeek.value % 7))
//    Log.d("CALENDAR", "day" + (firstDay.dayOfMonth))
//
//    Log.d("CALENDAR", "month : " + getMonthDays(month).toString())

    val pagerState = rememberPagerState(
        initialPage = 500,
        pageCount = { 1000 }
    )

    val scope = rememberCoroutineScope()

    val currentMonth = YearMonth.now()
        .plusMonths((pagerState.currentPage - 500).toLong())

    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(bottom = 12.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            IconButton(
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            pagerState.currentPage - 1,
                            animationSpec = tween(700)
                        )
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_left),
                    contentDescription = null
                )
            }

            Text(
                text = "${
                    currentMonth.month.name.lowercase()
                        .replaceFirstChar { it.uppercase() }
                } ${currentMonth.year}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
            )

            IconButton(
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            pagerState.currentPage + 1,
                            animationSpec = tween(700)
                        )
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_right),
                    contentDescription = null
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            DayOfWeek.entries.forEach {
                Text(text = it.abbr.take(1), modifier = Modifier.padding(bottom = 8.dp))
            }
        }

        Box(
            modifier = Modifier
                .wrapContentSize(
                    Alignment.TopCenter,
                    )
                .animateContentSize(
                    animationSpec = tween(300)
                )
        ) {
            HorizontalPager(
                beyondViewportPageCount = 1,
                contentPadding = PaddingValues(0.dp),
                state = pagerState,
                key = { page -> page },
                modifier = Modifier
            ) { page ->

//                val month = YearMonth.now()
//                    .plusMonths((page - 500).toLong())

                val currentMonth = YearMonth.now()
                    .plusMonths((pagerState.currentPage - 500).toLong())

                val days = getMonthDays(currentMonth)

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    modifier = Modifier
                ) {

                    items(days) { date ->

                        val border =
                            if (selectedDay == date && date != LocalDate.now()) {
                                BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                            } else {
                                BorderStroke(0.dp, Color.Transparent)
                            }

                        val isCurrentMonth = date.month == currentMonth.month

                        Column(
                            modifier = Modifier,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = date?.dayOfMonth?.toString() ?: "",
                                textAlign = TextAlign.Center,
                                color = if (LocalDate.now() == date) MaterialTheme.colorScheme.onPrimary else if (isCurrentMonth) MaterialTheme.colorScheme.onSurface else Color.Gray,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .padding(8.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        if (isCurrentMonth) {
                                            onSelect(date)
                                        }
                                    }
                                    .background(
                                        if (date == LocalDate.now()) Color.Magenta
                                        else Color.Transparent
                                    )
                                    .border(border, CircleShape)
                                    .wrapContentSize(Alignment.Center)
                            )

                            if (imageDates.contains(date)) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 2.dp)
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }


                    }
                }
            }
        }
    }
}

fun getMonthDays(month: YearMonth): List<LocalDate> {

    val days = mutableListOf<LocalDate>()

    val firstDayOfMonth = month.atDay(1)
    val startOffset = firstDayOfMonth.dayOfWeek.value % 7

    val prevMonth = month.minusMonths(1)
    val prevMonthDays = prevMonth.lengthOfMonth()

    for (i in startOffset downTo 1) {
        days.add(prevMonth.atDay(prevMonthDays - i + 1))
    }

    for (day in 1..month.lengthOfMonth()) {
        days.add(month.atDay(day))
    }

    val nextMonth = month.plusMonths(1)
    var nextDay = 1

    while (days.size % 7 != 0) {
        days.add(nextMonth.atDay(nextDay++))
    }

    return days
}