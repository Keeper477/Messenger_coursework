var stompClient = null;

function connect() {
    var socket = new SockJS('/message-ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messages', function (message) {
            if (JSON.parse(message.body).toUser === document.getElementById("orig_name").innerText) {
                let dict = JSON.parse(message.body);
                let div = document.getElementById("messages");
                let innerDiv = document.createElement('div');
                innerDiv.className = 'chat-message-left pb-4';
                let flexDiv = document.createElement("div");
                flexDiv.className = 'flex-shrink-1 bg-light rounded py-2 px-3 mr-3';
                let label = document.createElement("label");
                label.innerText = dict.content;
                innerDiv.appendChild(flexDiv);
                flexDiv.appendChild(label);
                div.appendChild(innerDiv);
                window.scrollTo(0, document.body.scrollHeight);
            }
        });
    });
}

function sendName() {
    stompClient.send("/app/send", {}, JSON.stringify({
        'toUser': document.getElementById("name").innerText,
        'fromUser': document.getElementById("orig_name").innerText,
        'content': document.getElementById("content").value
    }));
}

$(function () {
    $("#send_button").click(function () {
        sendName();
    });
});

connect();
window.scrollTo(0, document.body.scrollHeight);