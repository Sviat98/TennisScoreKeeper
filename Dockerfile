# Используем образ с Java и Gradle для сборки
FROM gradle:latest AS build

# Копируем исходный код в контейнер
WORKDIR /app
COPY . .

# Собираем проект
RUN gradle jsBrowserProductionWebpack

# Используем легковесный веб-сервер для статики
FROM nginx:alpine

# Копируем собранные файлы из папки build/distributions в nginx
COPY --from=build /app/build/distributions /usr/share/nginx/html

# Настраиваем nginx (опционально)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Порт, который слушает nginx
EXPOSE 80

# Запускаем nginx
CMD ["nginx", "-g", "daemon off;"]