 var stompClient = null;
document.addEventListener('DOMContentLoaded', () => {
    let token = localStorage.getItem('jwtToken');
    const socket = new SockJS('/spring/websocket');
    stompClient = Stomp.over(socket);

    stompClient.connect({'Authorization': token}, function(frame) {
        console.log('Conectado: ' + frame);
        stompClient.subscribe('/user/queue/mensajes', function(message) {
            console.log('Mensaje recibido: ' + message.body);
            mostrarMensaje(JSON.parse(message.body));
        });
    });

})

 function mostrarMensaje(mensaje) {
     let nombre = localStorage.getItem("username");
     const contenedor = document.getElementById("chatMessages");

     const divMensaje = document.createElement("div");
     const remitente = document.createElement("strong"); // para el nombre arriba
     remitente.textContent = mensaje.remitente + ":";

     const contenido = document.createElement("div");
     contenido.textContent = mensaje.mensaje;

     if (mensaje.remitente === nombre) {
         divMensaje.className = "message sent";
     } else {
         divMensaje.className = "message received";
     }

     divMensaje.appendChild(remitente);
     divMensaje.appendChild(contenido);
     contenedor.appendChild(divMensaje);
 }

function sendMessage(text) {

    let destinatario = document.getElementById("destinatario").value;
    console.log("mensaje",text);
    const mensaje = {
        destinatario: destinatario,
        contenido: text
    };

    document.getElementById("mensaje").value = "";
    stompClient.send('/app/chat.private', {}, JSON.stringify(mensaje));
}



document.getElementById("enviar").addEventListener("click", (e) => {

    let mensaje = document.getElementById("mensaje").value;
    if(mensaje === ""){
        return
    }
    sendMessage(mensaje);


})