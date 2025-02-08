# Используем образ с Java и Gradle для сборки
FROM gradle:latest AS build

# Копируем исходный код в контейнер
WORKDIR /app
COPY . .

####### ОТЛАДКА!!!!!

# Устанавливаем tree для визуализации структуры файлов
RUN apt-get update && apt-get install -y tree

####### КОНЕЦ ОТЛАДКИ!!!!!

WORKDIR /app/composeApp
# Собираем проект
# для релиза
# RUN gradle wasmJsBrowserDistribution
# для дебага
RUN gradle wasmJsBrowserDevelopmentWebpack

####### ОТЛАДКА!!!!!

# # Выводим дерево файлов после сборки
# RUN echo "File structure after build:" && tree /app -L 5

# Выводим структуру файлов для отладки
RUN ls -lR /app/composeApp/build

####### КОНЕЦ ОТЛАДКИ!!!!!

# Этап запуска (Nginx для статики)
FROM nginx:alpine

# Копируем собранные файлы из productionExecutable
# релиз
# COPY --from=build /app/composeApp/build/dist/wasmJs/productionExecutable /usr/share/nginx/html
# дебаг
COPY --from=build /app/composeApp/build/kotlin-webpack/wasmJs/developmentExecutable /usr/share/nginx/html

# Фикс для маршрутизации в SPA (если используется роутинг)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]