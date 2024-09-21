package com.kire.audio.presentation.util.search

/**
 * Вычисляет расстояние левенштейна между двумя последовательностями символов.
 *
 * @param lhs Первая последовательность
 * @param rhs Вторая последовательность
 *
 * @return Растояние Левенштейна
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
internal fun levenshteinDistance(lhs: CharSequence, rhs: CharSequence): Int {
    /** Длина первой последовательности символов */
    val lhsLength = lhs.length
    /** Длина второй последовательности символов */
    val rhsLength = rhs.length
    /** Матрица "расстояний" / различий между символами последовательности */
    val distance = MutableList(lhsLength + 1) { MutableList(rhsLength + 1) { 0 } }

    /** Заполняем первый столбец */
    for (i in 0..lhsLength)
        distance[i][0] = i

    /** Заполняем первую строку */
    for (j in 0..rhsLength)
        distance[0][j] = j

    /** Заполняем оставшуюся часть таблицы */
    for (i in 1..lhsLength)
        for (j in 1..rhsLength) {

            /** Выясняем, совпадают ли символы на одинаковых позициях: 0 - да, 1 - нет */
            val cost = if (lhs[i - 1] == rhs[j - 1]) 0 else 1

            /** Заполняем i-ую j-ую ячейку таблицы.
             * Чем более похожи символьные последовательности, тем менее стремительно возрастают значения на главной диагонали.
             * Если последовательности совпадают, то по диагонали будут все нули */
            distance[i][j] = minOf(
                distance[i - 1][j] + 1,
                distance[i][j - 1] + 1,
                distance[i - 1][j - 1] + cost
            )
        }

    /** Возвращаем крайний правый снизу элемент матрицы. Чем ниже число, тем более похожи последовательности */
    return distance[lhsLength][rhsLength]
}
