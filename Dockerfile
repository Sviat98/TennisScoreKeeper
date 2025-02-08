# Используем образ с Java и Gradle для сборки
FROM gradle:latest AS build

# Копируем исходный код в контейнер
WORKDIR /app
COPY . .

# Собираем проект
RUN gradle wasmJsBrowserDevelopmentExecutableDistribution

# Этап запуска (Nginx для статики)
FROM nginx:alpine

# Копируем собранные файлы из productionExecutable
COPY --from=build /app/composeApp/build/dist/wasmJs/productionExecutable /usr/share/nginx/html

# Фикс для маршрутизации в SPA (если используется роутинг)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]