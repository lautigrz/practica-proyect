let botonIngresar = document.getElementById("ingresar");
var stompClient = null;
botonIngresar.addEventListener("click", (e) => {
    localStorage.removeItem("jwtToken"); // o el nombre real que estés usando

    let nombre = document.getElementById("user").value;
    let contrasenia = document.getElementById("pass").value;

    fetch("/spring/login-chat-control", {

        method: "POST",
        body: JSON.stringify({usuario: nombre, password: contrasenia}),
        headers: {'Content-Type' : 'application/json'}
    })
        .then(response => response.json())
        .then(data => {
            console.log(data.token);
            localStorage.setItem("jwtToken", data.token);

            document.querySelector('.form-container').style.display = "none";
            document.querySelector('.chat-container').style.display = "flex";

            connect(data.token);

        })
})


function connect(token) {
    const socket = new SockJS('/spring/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({'Authorization': token}, function(frame) {
        console.log('Conectado: ' + frame);
        stompClient.subscribe('/topic/grupo', function(message) {
            console.log('Mensaje recibido: ' + message.body);
            mostrarMensaje(JSON.parse(message.body));
        });
    });
}

function mostrarMensaje(mensaje) {

    let nombre = document.getElementById("user").value;
    const contenedor = document.getElementById("chatMessages");
    const divMensaje = document.createElement("div");

    if (mensaje.nombre === nombre) {
        divMensaje.className = "message sent";
    } else {
        divMensaje.className = "message received";
    }

    divMensaje.textContent = mensaje.mensaje;
    contenedor.appendChild(divMensaje);
}


function sendMessage(text) {

    let token = localStorage.getItem("jwtToken");
    let nombre = document.getElementById("user").value;
    console.log("mensaje",text);
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