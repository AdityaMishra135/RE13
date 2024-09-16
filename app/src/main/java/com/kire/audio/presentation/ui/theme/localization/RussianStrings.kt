package com.kire.audio.presentation.ui.theme.localization

object RussianStrings: IStrings {
    override val dropdownDate: String = "Дата"
    override val dropdownArtist: String = "Исполнитель"
    override val dropdownDuration: String = "Длительность"
    override val dropdownTitle: String = "Название"
    override val infoDialogHeader: String = "Данные"
    override val infoDialogTitle: String = "Название"
    override val infoDialogArtist: String = "Исполнитель"
    override val infoDialogAlbum: String = "Альбом"
    override val infoDialogDuration: String = "Длительность"
    override val infoDialogFavourite: String = "Любимое"
    override val infoDialogDateAdded: String = "Дата добавления"
    override val infoDialogAlbumId: String = "ID альбома"
    override val infoDialogImageUri: String = "URI картинки"
    override val infoDialogPath: String = "Путь"
    override val favouriteDialogHeader: String = "Любимое"
    override val lyricsDialogHeader: String = "Текст"
    override val lyricsDialogWaitingMessage: String = "Ожидание"
    override val lyricsDialogUnsuccessfulMessage: String =
        "Текст песни не может быть получен.\n\n" +
                "1) Проверьте подключение к интернету.\n\n" +
                "2) Проверьте, что имя автора и название трека состоит только из латинских символов, цифр и специальных знаков.\n\n" +
                "3) Проверьте, чтобы в имени автора или названии не было ошибок.\n\n"
    override val nothingWasFound: String = "Нет данных"
    override val editModeText: String = "Редактировать текущий текст"
    override val byArtistAndTitleModeText: String = "По имени исполнителя и названию трека"
    override val byArtistAndTitleModeTextExample: String = "Пример запроса: while she sleeps feels"
    override val byGeniusLinkModeTextExample: String = "Пример ссылки: https://genius.com/While-she-sleeps-feel-lyrics"
    override val byGeniusLinkModeText: String = "По Genius ссылке"
    override val automaticModeText: String = "Автоматически"
    override val listScreenHeader: String = "Все треки"
    override val listScreenSearchHint: String = "Поиск"
    override val albumScreenHeader: String = "Альбомы"
    override val yes: String = "Да"
    override val no: String = "Нет"
    override val notificationContentTitle: String = "Meowdio уведомление"
    override val notificationContentText: String = "Музыка не играет"
    override val error: String = "Ошибка"
}