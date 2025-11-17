# Используем образ с Java и Gradle для сборки
FROM gradle:9.0.0-jdk21 AS build

ARG BUILD_MODE=debug
ENV BUILD_MODE=$BUILD_MODE

# Копируем исходный код в контейнер
WORKDIR /app
COPY . .

# Устанавливаем tree для визуализации структуры файлов
RUN apt-get update && apt-get install -y tree

WORKDIR /app/composeApp

## перед сборкой удаляем ненужные исполняемые файлы из bild-директорий
RUN rm -rf /app/composeApp/build/dist/wasmJs/* && \
    rm -rf /usr/share/nginx/html/*

# Собираем проект в заивсимости от BUILD_MODE
RUN if [ "$BUILD_MODE" = "release" ]; then \
        GRADLE_TASK=wasmJsBrowserDistribution && \
        DIST_DIR=productionExecutable; \
    else \
        GRADLE_TASK=wasmJsBrowserDevelopmentExecutableDistribution && \
        DIST_DIR=developmentExecutable; \
    fi && \
    echo ">>> BUILD_MODE=$BUILD_MODE" && \
    echo ">>> GRADLE_TASK=$GRADLE_TASK" && \
    echo ">>> DIST_DIR=$DIST_DIR" && \
    gradle $GRADLE_TASK && \
    cp -r build/dist/wasmJs/$DIST_DIR /output

RUN cat /app/composeApp/build/generated/source/buildConfig/commonMain/kotlin/BuildConfig.kt

####### ОТЛАДКА!!!!!
RUN ls -lR /output
####### КОНЕЦ ОТЛАДКИ!!!!!

# ---------- RUNTIME STAGE ----------
# Этап запуска (Nginx для статики)
FROM nginx:alpine

# Копируем собранные файлы из output
COPY --from=build /output /usr/share/nginx/html

# Выводим структуру файлов для отладки
RUN ls -lR /usr/share/nginx/html

# Фикс для маршрутизации в SPA (если используется роутинг)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]