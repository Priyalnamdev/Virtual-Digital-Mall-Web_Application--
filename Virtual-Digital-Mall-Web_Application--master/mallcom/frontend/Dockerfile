FROM nginx:alpine

# Copy frontend files into Nginx web directory
COPY . /usr/share/nginx/html

# Nginx listens on port 80
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]