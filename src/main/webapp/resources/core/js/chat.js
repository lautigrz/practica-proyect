
var stompClient = null;

document.addEventListener("DOMContentLoaded", function() {

    let token = localStorage.getItem("jwtToken");
    connect(token);
})



function connect(token) {
    const socket = new SockJS('/spring/websocket');
    let username = localStorage.getItem("username");
    stompClient = Stomp.over(socket);
    stompClient.connect({'Authorization': token}, function(frame) {
        console.log('Conectado: ' + frame);
        stompClient.subscribe('/topic/grupo', function(message) {
            console.log('Mensaje recibido: ' + message.body);
            mostrarMensaje(JSON.parse(message.body));
        });
        stompClient.subscribe('/user/queue/desconectar', function (mensaje) {
            alert(mensaje.body); // o mostrar modal
            stompClient.disconnect(() => {
                console.log("Desconectado por duplicidad de sesión");
            });
        });


        stompClient.send(
            "/app/chat.register",
            {},
            JSON.stringify({ username: username, estado: "CONECTADO" })
        );
    });
}

function mostrarMensaje(mensaje) {

    let nombre = localStorage.getItem("username");
    const contenedor = document.getElementById("chatMessages");
    const divMensaje = document.createElement("div");


    if (mensaje.estado === "CONECTADO") {
        divMensaje.className = "info-message"; // un estilo para info
        divMensaje.textContent = `${mensaje.username} se conectó`;
    } else if (mensaje.estado === "DESCONECTADO") {
        divMensaje.className = "info-message"; // estilo para info
        divMensaje.textContent = `${mensaje.username} se desconectó`;
    }else if (mensaje.nombre === nombre) {
        divMensaje.className = "message sent";
        divMensaje.textContent = mensaje.mensaje;
    } else {
        divMensaje.className = "message received";
        divMensaje.textContent = mensaje.mensaje;
    }


    contenedor.appendChild(divMensaje);
}


function sendMessage(text) {


    let nombre = localStorage.getItem("username");

    let mensajeObj = {
        nombre: nombre,
        mensaje: text
    };

    document.getElementById("mensaje").value = "";
    stompClient.send('/app/chat', {}, JSON.stringify(mensajeObj));
}



document.getElementById("enviar").addEventListener("click", (e) => {

    let mensaje = document.getElementById("mensaje").value;
    if(mensaje === ""){
        return
    }
    sendMessage(mensaje);


})