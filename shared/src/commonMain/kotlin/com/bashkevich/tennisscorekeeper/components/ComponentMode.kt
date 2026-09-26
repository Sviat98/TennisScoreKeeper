package com.bashkevich.tennisscorekeeper.components

/**
 * Режим работы компонента: ADD — создание (полный ввод), EDIT — редактирование
 * уже существующей сущности (часть полей, например выбор участника, залочена).
 */
enum class ComponentMode { ADD, EDIT }
