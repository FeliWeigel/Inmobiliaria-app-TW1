document.addEventListener("DOMContentLoaded", () => {
    const propiedadId = document.getElementById('rango-fechas').dataset.propiedadId;
    const fetchURL = `http://localhost:8080/spring/propiedad/${propiedadId}/reservas`;
    // llamada al servidor para obtener las fechas reservadas de una propiedad y posteriormente marcarlas en rojo dentro del calendario dinamicamente
    fetch(fetchURL)
        .then(res => res.json())
        .then(data => {
            let fechasArray = []
            if(data){
                data.forEach((fechasAlquiler) => {
                    let fechaInicio = new Date(fechasAlquiler.fechaInicio);
                    let fechaFin = new Date(fechasAlquiler.fechaFin)

                    fechasArray.push({
                        from: fechaInicio,
                        to: fechaFin
                    })
                })
            }
            mostrarCalendario(fechasArray)
        })
        .catch(err => console.log(err))

    // calendario
    function mostrarCalendario(fechasReservadas){
        flatpickr("#rango-fechas", {
            mode: "range",
            dateFormat: "Y-m-d",
            inline: true,
            locale: {
                rangeSeparator: ' a: '
            },
            disable: fechasReservadas,
            onChange: function(selectedDates,dateStr, instance) {
                marcarFechasYaReservadas(instance)
                if (selectedDates.length === 2) {
                    const startDate = selectedDates[0];
                    const endDate = selectedDates[1];
                    document.getElementById('start_date').value = flatpickr.formatDate(startDate, "Y-m-d");
                    document.getElementById('end_date').value = flatpickr.formatDate(endDate, "Y-m-d");
                    const rangoFechasTexto = document.getElementById('rango-fechas-texto');
                    rangoFechasTexto.innerHTML = `<strong>de:</strong> ${flatpickr.formatDate(startDate, "d-m-Y")} <strong>hasta:</strong> ${flatpickr.formatDate(endDate, "d-m-Y")}`;
                }
            },
            onReady: function(selectedDates, dateStr, instance) {
                marcarFechasYaReservadas(instance)
            },
            onMonthChange: function(selectedDates, dateStr, instance) {
                marcarFechasYaReservadas(instance);
            }
        });
    }

    function marcarFechasYaReservadas(instance){
        instance.days.childNodes.forEach(node => {
            if(node.classList.contains("flatpickr-disabled")){
                node.style.background= "#ffcccc"
                node.style.color= "#000"
            }
        });
    }

    // manejo de alertas de error/exito al ejecutar un nuevo alquiler
    let successMessage = document.getElementById("alquiler-success")
    let errorMessage = document.getElementById("alquiler-error")
    if(successMessage){
        successMessage.style.opacity = '1'
    }else if(errorMessage){
        errorMessage.style.opacity = '1'
    }

    setTimeout(() => {
        if(successMessage){
            successMessage.style.opacity = '0'
        }else if(errorMessage){
            errorMessage.style.opacity = '0'
        }
    }, 5000)
});