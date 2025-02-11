let socket = new WebSocket("ws://localhost:9000/ws");

socket.onopen = function () {
    console.log("Connected to WebSocket server");
    socket.send("Hello Server!");
};

socket.onmessage = function (event) {
    console.log("Received: " + event.data);
};

socket.onclose = function () {
    console.log("WebSocket connection closed");
};
