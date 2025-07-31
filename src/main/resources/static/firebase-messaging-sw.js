// firebase-messaging-sw.js
importScripts('https://www.gstatic.com/firebasejs/9.22.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/9.22.0/firebase-messaging-compat.js');

firebase.initializeApp({
  apiKey: "AIzaSyBJ9T7mt7CtwZm1E89qLK-1XeRitcwV-Es",
  authDomain: "fcmtest-bd402.firebaseapp.com",
  projectId: "fcmtest-bd402",
  storageBucket: "fcmtest-bd402.firebasestorage.app",
  messagingSenderId: "603915203012",
  appId: "1:603915203012:web:fb00e2ef0dab3fb51ef491"
});

const messaging = firebase.messaging();

messaging.onBackgroundMessage(function (payload) {
  console.log('[firebase-messaging-sw.js] Received background message: ', payload);

  const notificationTitle = payload.notification.title;
  const notificationOptions = {
    body: payload.notification.body,
    icon: '/icon.png' // 선택사항
  };

  self.registration.showNotification(notificationTitle, notificationOptions);
});
