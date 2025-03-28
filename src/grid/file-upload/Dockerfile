FROM node:12.22.12@sha256:01627afeb110b3054ba4a1405541ca095c8bfca1cb6f2be9479c767a2711879e

WORKDIR /usr/src/app
COPY package*.json ./
RUN npm config set registry https://nexus.nonprod.dwpcloud.uk/repository/npm/ && npm install
COPY start.js ./

EXPOSE 3000
CMD [ "node", "start.js" ]