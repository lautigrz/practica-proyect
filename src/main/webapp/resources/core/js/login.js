let botonIngresar = document.getElementById("ingreso");

botonIngresar.addEventListener("click", (e) => {
    localStorage.removeItem("jwtToken");

    let nombre = document.getElementById("username").value;
    let contrasenia = document.getElementById("password").value;

    fetch("/spring/login-chat-control", {

        method: "POST",
        body: JSON.stringify({usuario: nombre, password: contrasenia}),
        headers: {'Content-Type' : 'application/json'}
    })
        .then(response => response.json())
        .then(data => {
            console.log(data.token);
            localStorage.setItem("jwtToken", data.token);
            localStorage.setItem("username",nombre);
            window.location.href = "/spring/chat-privado";
        })
})