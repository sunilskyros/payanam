const CACHE_NAME = "payanam-cache-v1";
const ASSETS_TO_CACHE = [
  "/",
  "/index.html",
  "/dashboard.html",
  "/collector.html",
  "/admin.html",
  "/src/css/style.css",
  "/src/js/index.js",
  "/src/images/icon.png",
  "https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap",
  "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css",
  "https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.6.1/sockjs.min.js",
  "https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"
];

// Install Service Worker and Precache Assets
self.addEventListener("install", (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
      return cache.addAll(ASSETS_TO_CACHE);
    })
  );
  self.skipWaiting();
});

// Activate Service Worker and clean up old caches
self.addEventListener("activate", (event) => {
  event.waitUntil(
    caches.keys().then((cacheNames) => {
      return Promise.all(
        cacheNames.map((cache) => {
          if (cache !== CACHE_NAME) {
            return caches.delete(cache);
          }
        })
      );
    })
  );
  self.clients.claim();
});

// Fetch events with Stale-While-Revalidate strategy for static resources
self.addEventListener("fetch", (event) => {
  // Avoid caching non-GET requests or dynamic api calls
  const url = event.request.url;
  if (
    event.request.method !== "GET" || 
    url.includes("/api/") || 
    url.includes("/login") || 
    url.includes("/register") || 
    url.includes("/logout")
  ) {
    event.respondWith(
      fetch(event.request).catch(() => {
        // Fallback for offline API requests
        return new Response(
          JSON.stringify({ error: "Offline mode active. Using stored local data." }),
          {
            status: 503,
            headers: { "Content-Type": "application/json" }
          }
        );
      })
    );
    return;
  }

  // Intercept and cache-match
  event.respondWith(
    caches.match(event.request).then((cachedResponse) => {
      const fetchPromise = fetch(event.request)
        .then((networkResponse) => {
          if (networkResponse && networkResponse.status === 200) {
            caches.open(CACHE_NAME).then((cache) => {
              cache.put(event.request, networkResponse.clone());
            });
          }
          return networkResponse;
        })
        .catch(() => {
          // Silent catch for network request failure (offline)
        });
      
      // Return cached response instantly if available, fallback to network
      return cachedResponse || fetchPromise;
    })
  );
});

// Push notification updates listener
self.addEventListener("push", (event) => {
  let data = { title: "Payanam Live", body: "Live journey status sequence update." };
  if (event.data) {
    try {
      data = event.data.json();
    } catch (e) {
      data = { title: "Payanam Live", body: event.data.text() };
    }
  }

  const options = {
    body: data.body,
    icon: "/src/images/icon.png",
    badge: "/src/images/icon.png",
    vibrate: [200, 100, 200],
    data: {
      url: data.url || "/dashboard.html"
    }
  };

  event.waitUntil(
    self.registration.showNotification(data.title, options)
  );
});

// Handle push notification click and navigate to corresponding screen
self.addEventListener("notificationclick", (event) => {
  event.notification.close();
  event.waitUntil(
    clients.matchAll({ type: "window" }).then((clientList) => {
      const targetUrl = event.notification.data.url;
      for (let i = 0; i < clientList.length; i++) {
        const client = clientList[i];
        if (client.url.includes(targetUrl) && "focus" in client) {
          return client.focus();
        }
      }
      if (clients.openWindow) {
        return clients.openWindow(targetUrl);
      }
    })
  );
});
