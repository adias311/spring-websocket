  
const domain = window.location.protocol + '//' + window.location.hostname +":8080";
const imgPersonal = "https://res.cloudinary.com/daboj0tnc/image/upload/v1752335877/personal.png";
const imgGroup = "https://res.cloudinary.com/daboj0tnc/image/upload/v1752335559/group.jpg";

// get user 
const params = new URLSearchParams(window.location.search);
const username = params.get("username");
let chatName = "";
let chatRoomId = "";

console.log("Username:", username);     

jQuery.ajax({
    url: domain+"/api/v1/users",
    type: "POST",
    contentType: "text/plain",
    data: username,
    dataType: "json",
    success: function(response) {

        console.log("Data user:", response);

        var personal = $('#personal');
        var group = $('#group');
        let c = 0;

       Object.entries(response.rooms).forEach(([room, type], index) => {
            // Buat elemen baru    
            
            const [userA, userB] = room.split("#").map(s => s.trim());
            chatName = userA === username ? userB : userA;

            var chat = $(`
                <a href="#" class="d-flex align-items-center" data-room="${room}" data-type="${type}">
                    <div class="flex-shrink-0">
                        <img class="img-fluid"
                        style="width: 40px; height: 40px; border-radius: 50%;"
                        src="${type === "personal" ? imgPersonal : imgGroup}" 
                        alt="user img">
                        <span class="${(c === 0 && type === "personal") ? "active" : ""}"></span>
                    </div>
                    <div class="flex-grow-1 ms-3">
                        <h3>${chatName}</h3>
                        <p>back end developer</p>
                    </div>
                </a>
            `);

             if(c === 0 && type === "personal") {
                c++;
                chatRoomId = room;
                activateChatBox({roomId: room, type: type}); 
                loadChat(room);
            }

            if (type === "group") group.append(chat);
            else personal.append(chat);

        });

        $(".chat-list a").click(function () {

            const roomId = $(this).data('room');
            chatRoomId = roomId;
            subsTopic(chatRoomId);        

            const type = $(this).data('type');
            $(".chatbox").addClass('showbox');
            activateChatBox({roomId, el: $(this), type});
            loadChat(roomId);

            return false; 
        });

        $(".chat-icon").click(function() {
            $(".chatbox").removeClass('showbox');
        });
    }
});

// get messages and load chat
const loadChat = (roomId) => {
    const url = domain + `/api/v1/rooms/${encodeURIComponent(roomId)}/messages`;
    jQuery.ajax({
        url: url,
        type: "GET",
        success: function(response) {
           console.log("Chat messages:", response);

           response.forEach((message) => {
               appendChatMessage(message);
           })

            scrollToBottom();

        },
        error: function(xhr, status, error) {
            console.error("Error loading chat messages:", error);
        }
    });
};

// append chat message
const appendChatMessage = (message) => {
    const chatBox = $('.msg-body ul');
    let cls = "repaly";
    if(message.sender !== username) {
        cls = "sender";
    }
    chatBox.append(`
        <li class="${cls}">
        <p>${message.content}</p>
        <span class="time">${formatDate(message.timeStamp)}</span>
        </li>
    `);
};

// Activate chatbox
const activateChatBox = ({roomId, el = null, type = null}) => {
    $('.chat-list a span').removeClass('active');

    if (el && el.length) {
        const span = el.find('span');
        chatName = el.find('h3').text();
        if (span.length) {
            span.addClass('active');
        }
    }

    if (type === "personal") {
        $('.msg-head img').last().attr('src', imgPersonal);
    } else{
        $('.msg-head img').last().attr('src', imgGroup); 
    } 

    $('.msg-body ul').empty(); 
    $('.msg-head h3').text(chatName);

};

const formatDate = (isoDate) => {
    
    isoDate = isoDate.substring(0, 23); 

    let date = new Date(isoDate);
    
    let options = {
        year: 'numeric',
        month: 'long',  
        day: '2-digit',
        hour: 'numeric',
        minute: '2-digit',
        hour12: true
    };

    return date.toLocaleString(undefined, options);

}

// Popup add chat
jQuery.ajax({
    url: domain + "/api/v1/rooms",
    type: "GET",
    success: function(response) {
        renderChatList(response);
    },error: function(xhr, status, error) {
        console.error("Error loading personal chats:", error);
    }
});

function renderChatList(rooms) {
    rooms.map(room => {
        const { roomId, type } = room;
        const chatListHtml = `
            <div class="chat-list-item d-flex justify-content-between align-items-center">
                <span>${roomId}</span>
                <button class="btn btn-sm btn-primary" onclick="openChat('${roomId}', '${type}')">Chat</button>
            </div>
        `;
        type === "personal" ? $("#personalChats").append(chatListHtml) : $("#groupChats").append(chatListHtml);
    });
}

$(document).ready(function() {
    
    $("#openChatMenu").click(function() {
        $("#chatMenuPopup").fadeIn();
    });

    $("#closeChatMenu").click(function() {
        $("#chatMenuPopup").fadeOut();
    });

});

function openChat(name, type) {
    alert("Opening chat with: " + name + " Type: " + type);
    $("#chatMenuPopup").hide();

    jQuery.ajax({
        url: domain + "/api/v1/rooms",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({username, roomId: name, type }),
        success: function(response) {
            const { roomId, type } = response;
            console.log("Chat room created:", response);
            activateChatBox({roomId, type});
            loadChat(roomId);
        },
        error: function(xhr, status, error) {
            if (xhr.status === 409) {
               alert("Chat room already exists.");
            }
            console.error("Error creating chat room:", error);
        }
    });
}

// 🔌 Inisialisasi STOMP Client

const stompClient = new StompJs.Client({
      webSocketFactory: () => new SockJS(domain + '/ws'),
      reconnectDelay: 5000,
      onConnect: () => {
        console.log("✅ Terhubung ke WebSocket");
        subsTopic(chatRoomId);
      },

      onStompError: (frame) => {
        console.error("⛔ STOMP error:", frame);
      },

      onWebSocketError: (error) => {
        console.error("⛔ WebSocket error:", error);
      }
    });

    stompClient.activate();  
    
// subscribe to chat messages
  let currentSubscription = null;
  let onSubs = "";

    // Subscribe to chat messages
    const subsTopic = (chatRoomId) => {      

        if (currentSubscription) {
            currentSubscription.unsubscribe();
            console.log("🔌 Unsubscribed from:", onSubs);
        }

        onSubs = chatRoomId;

        currentSubscription = stompClient.subscribe(`/topic/room/${chatRoomId}`, (message) => {
            console.log("📩 Pesan diterima:", message.body);
            appendChatMessage(JSON.parse(message.body));
            scrollToBottom(); 
        });

        console.log("✅ Subscribed to:", chatRoomId);
    };    

    // 🔘 Event jQuery untuk tombol kirim
    $(document).ready(function () { 

        const messageContent = $('#messageInput');    
        
        $('form').on('submit', function (e) {
            e.preventDefault(); // Hindari reload
            $('#sendButton').click(); // Kirim pesan
        });
        
        $(messageContent).on('keydown', function (e) {
            if (e.key === 'Enter' || e.keyCode === 13) {
                e.preventDefault(); // Agar tidak membuat baris baru
                $('#sendButton').click(); // Trigger tombol kirim
            }
        });


        $('#sendButton').on('click', function () {
            console.log(chatRoomId, messageContent);   
            try {
                stompClient.publish({
                    destination: "/app/sendMessage",
                    body: JSON.stringify({
                        sender: username,
                        content: messageContent.val().trim(),
                        roomId: chatRoomId
                    })
                });
                messageContent.val(""); 
            } catch (error) {
                console.error("❌ Error saat publish message:", error);
                alert("Gagal mengirim pesan. Silakan coba lagi.");
            }
        });

        $("#profile").text(`👤 ${username}`);
    });

    // scroll
    const scrollToBottom = () => {
        const msgBody = $('.msg-body');
        msgBody.scrollTop(msgBody[0].scrollHeight);
    };

