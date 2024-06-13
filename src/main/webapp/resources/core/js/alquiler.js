document.addEventListener("DOMContentLoaded", () => {
    const propiedadId = document.getElementById('rango-fechas').dataset.propiedadId;
    const fetchURL = `http://localhost:8080/spring/propiedad/${propiedadId}/reservas`
    fetch(fetchURL)
        .then(res => res.json())
        .then(data => {
            let fechasArray = new Array()
            if(data){
                data.forEach((fechasAlquiler) => {
                    fechaInicio = new Date(fechasAlquiler.fechaInicio).toLocaleDateString()
                    fechaFin = new Date(fechasAlquiler.fechaFin).toLocaleDateString()

                    let rangoFechas = {
                        fechaInicio: fechaInicio,
                        fechaFin: fechaFin
                    }

                    fechasArray.push(rangoFechas)
                })
            }

            console.log(fechasArray)
        })

    flatpickr("#rango-fechas", {
        mode: "range",
        dateFormat: "Y-m-d",
        inline: true,
        locale: {
            rangeSeparator: ' a: '
        },
        onChange: function(selectedDates) {
            if (selectedDates.length === 2) {
                const startDate = selectedDates[0];
                const endDate = selectedDates[1];
                document.getElementById('start_date').value = flatpickr.formatDate(startDate, "Y-m-d");
                document.getElementById('end_date').value = flatpickr.formatDate(endDate, "Y-m-d");
                const rangoFechasTexto = document.getElementById('rango-fechas-texto');
                rangoFechasTexto.innerHTML = `<strong>de:</strong> ${flatpickr.formatDate(startDate, "d-m-Y")} <strong>hasta:</strong> ${flatpickr.formatDate(endDate, "d-m-Y")}`;
            }
        }
    });

    let successMessage = document.getElementById("alquiler-success")
    let errorMessage = document.getElementById("alquiler-error")
    if(successMessage){
        successMessage.style.opacity = '1'
    }
    if(errorMessage){
        errorMessage.style.opacity = '1'
    }
    setTimeout(() => {
        if(successMessage){
            successMessage.style.opacity = '0'
        }
        if(errorMessage){
            errorMessage.style.opacity = '0'
        }
    }, 5000)
});