# Use a Node.js image to build the React app
FROM node:18 AS build
WORKDIR /app

# Copy package.json and install dependencies
COPY package*.json ./
RUN npm install

# Copy the source code
COPY . .

# Build the React app for production
RUN npm run build

# Use a lightweight web server image to serve the built React app
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html

# Expose port 80 for HTTP
EXPOSE 80

# Start the web server
CMD ["nginx", "-g", "daemon off;"]
