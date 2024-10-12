package com.kire.audio.presentation.util.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset

/**
 * Лямбда вариант стандартного паддинга.
 * Не триггерит рекомпозицию, подходит
 * для изменяемых во время работы значений
 * отступов, например, анимируемых.
 *
 * @param top отступ сверху
 * @param bottom отступ снизу
 * @param start отступ справа
 * @param end отступ слева
 * @param all отступы со всех сторон
 *
 * @return Modifier с указанными отступами
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
fun Modifier.dynamicPadding(
    top: () -> Dp = { 0.dp },
    bottom: () -> Dp = { 0.dp },
    start: () -> Dp = { 0.dp },
    end: () -> Dp = { 0.dp },
    all: () -> Dp = { 0.dp }
): Modifier =

    this.layout { measurable, constraints ->

        /** Суммарный горизонтальный отступ */
        val horizontal =
            if (all() != 0.dp)
                all().roundToPx() * 2
            else start().roundToPx() + end().roundToPx()

        /** Суммарный вертикальный отступ */
        val vertical =
            if (all() != 0.dp)
                all().roundToPx() * 2
            else top().roundToPx() + bottom().roundToPx()

        /** Измеряем размер содержимого */
        val placeable = measurable.measure(constraints.offset(-horizontal, -vertical))

        /** Ближайшее значение ширины, удовлетворяющее ограничениям */
        val width = constraints.constrainWidth(placeable.width + horizontal)
        /** Ближайшее значение высоты, удовлетворяющее ограничениям */
        val height = constraints.constrainHeight(placeable.height + vertical)

        /** Размещаем элементы */
        layout(width, height) {
            if (all() != 0.dp)
                placeable.placeRelative(all().roundToPx(), all().roundToPx())
            else
                placeable.placeRelative(start().roundToPx(), top().roundToPx())
        }
    }