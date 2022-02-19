let stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    const socket = new SockJS('/stomp-endpoint');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body));
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
}

function showPosts(post) {
    $("#posts").append("<tr><td>" + post.postText + "</td></tr>");
    $("#posts").append("<tr>\n" +
        "                <th scope=\"row\">" + post.postId + "</th>\n" +
        "                <td>" + post.title +"</td>\n" +
        "                <td th:text=\"${post.getPostText()}\"></td>\n" +
        "                <td th:text=\"${post.getPostDate()}\"></td>\n" +
        "                <td th:text=\"${post.getUser().getUserName()}\"></td>\n" +
        "                <td><a th:href=\"@{|/post/${post.getId()}|}\">View</a></td>\n" +
        "            </tr>")
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});