// const mp = new MercadoPago("APP_USR-ce16a581-25f1-479c-b2bb-db5ad1368dc9", {
//     locale: "es-AR", // The most common are: 'pt-BR', 'es-AR' and 'en-US'
// });
document.addEventListener("DOMContentLoaded", async () => {
    // PAGO MP
    const response = await fetch('http://localhost:8080/spring/create_preference' , {
        method: "POST"
    });

    if (!response.ok) {
        throw new Error('Network response was not ok');
    }
    const preference = await response.json();
    const preferenceId = preference.id;

    const mp = new MercadoPago("APP_USR-ce16a581-25f1-479c-b2bb-db5ad1368dc9", {
        locale: "es-AR"
    });
    const bricksBuilder = mp.bricks();

    bricksBuilder.create("wallet", "wallet_container", {
        initialization: {
            preferenceId: preferenceId,
        },
        customization: {
            texts: {
                valueProp: 'smart_option',
            },
        },
    });

    // PAGO EN EFECTIVO
    const pagarEfectivoBtn = document.getElementById("pagar-efectivo");

    pagarEfectivoBtn.addEventListener("click", async () => {
        let valorSeniaElement = document.getElementById('valor-senia');
        const valorSenia = valorSeniaElement ? valorSeniaElement.textContent.replace(/[^\d.-]/g, '') : null;

        try {
            const response = await fetch(`http://localhost:8080/spring/generate_invoice?precioReserva=${valorSenia}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/pdf'
                }
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'factura.pdf';
            document.body.appendChild(a);
            a.click();
            a.remove();
        } catch (error) {
            console.error('Error generating PDF:', error);
        }
    });

    // ALERTAS
    let successMessage = document.getElementById("pago-success")
    let errorMessage = document.getElementById("pago-error")
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

})