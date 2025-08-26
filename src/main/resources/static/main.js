const firebaseConfig = {
  apiKey: "AIzaSyBJ9T7mt7CtwZm1E89qLK-1XeRitcwV-Es",
  authDomain: "fcmtest-bd402.firebaseapp.com",
  projectId: "fcmtest-bd402",
  storageBucket: "fcmtest-bd402.firebasestorage.app",
  messagingSenderId: "603915203012",
  appId: "1:603915203012:web:fb00e2ef0dab3fb51ef491"
};

// ✅ compat 방식으로 초기화
firebase.initializeApp(firebaseConfig);

const messaging = firebase.messaging();

// 알림 권한 요청 및 토큰 발급
function requestPushPermission() {
  navigator.serviceWorker.register("/firebase-messaging-sw.js")
    .then((registration) => {
      Notification.requestPermission().then((permission) => {
        if (permission === "granted") {
          messaging.getToken({
            vapidKey: "BFD_AQGpmHFhetKPS9Y3SKeF9j5iLdxF6v1gvMvYSCvDRRlRV3MkgpZKoPuKAd-LvkvY2cyXSuxcLkdDwoz6RdE",
            serviceWorkerRegistration: registration
          }).then((token) => {
            console.log("✅ FCM Token:", token);
            alert("📱 모바일 FCM 토큰:\n" + token);

            //fetch("https://47279e5b2b9a.ngrok-free.app/api/fcm-token", {
            fetch("http://localhost:100000/api/users/fcm-token", {
              method: "POST",
              headers: {
                "Content-Type": "application/json"
              },
              body: JSON.stringify({ token })
            });

            document.body.insertAdjacentHTML("beforeend", `<p>FCM Token: <br><code>${token}</code></p>`);
          }).catch((err) => {
            console.error("❌ 토큰 가져오기 실패:", err);
          });
        } else {
          console.warn("🔒 알림 권한 거부됨");
        }
      });
    }).catch((err) => {
      console.error("❌ Service Worker 등록 실패:", err);
    });
}

// 포그라운드 메시지 수신 처리
messaging.onMessage((payload) => {
  console.log("🔔 Foreground Message:", payload);
  new Notification(payload.notification.title, {
    body: payload.notification.body
  });
});