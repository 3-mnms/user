importScripts('[https://www.gstatic.com/firebasejs/10.7.0/firebase-app-compat.js](https://www.gstatic.com/firebasejs/10.7.0/firebase-app-compat.js)');
importScripts('[https://www.gstatic.com/firebasejs/10.7.0/firebase-messaging-compat.js](https://www.gstatic.com/firebasejs/10.7.0/firebase-messaging-compat.js)');

// Firebase 프로젝트 설정을 초기화합니다.
const firebaseConfig = {
  apiKey: "AIzaSyBJ9T7mt7CtwZm1E89qLK-1XeRitcwV-Es",
  authDomain: "fcmtest-bd402.firebaseapp.com",
  projectId: "fcmtest-bd402",
  storageBucket: "fcmtest-bd402.firebasestorage.app",
  messagingSenderId: "603915203012",
  appId: "1:603915203012:web:fb00e2ef0dab3fb51ef491"
};

firebase.initializeApp(firebaseConfig);
const messaging = firebase.messaging();

// 백그라운드 메시지 수신 처리 (브라우저가 닫혀 있을 때)
messaging.onBackgroundMessage(function (payload) {
  console.log('[firebase-messaging-sw.js] 백그라운드 메시지 수신: ', payload);

  const notificationTitle = payload.notification.title;
  const notificationOptions = {
    body: payload.notification.body,
    icon: '/icon.png' // 알림에 표시할 아이콘 (선택사항)
  };

  // 알림을 표시합니다.
  return self.registration.showNotification(notificationTitle, notificationOptions);
});