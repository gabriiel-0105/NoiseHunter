FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci --include=optional
COPY . .
# Em producao, a API usa /api na mesma origem, via proxy do nginx.
RUN printf 'VITE_APP_URL_SERVER=\nVITE_APP_PORT_SERVER=\n' > .env.production && npm run build

FROM nginx:1.27-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
