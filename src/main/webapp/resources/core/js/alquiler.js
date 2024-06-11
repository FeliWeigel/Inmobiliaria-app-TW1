document.addEventListener("DOMContentLoaded", () => {
    flatpickr("#rango-fechas", {
        mode: "range",
        dateFormat: "Y-m-d",
        inline: true,
        onChange: function(selectedDates) {
            if (selectedDates.length === 2) {
                const startDate = selectedDates[0];
                const endDate = selectedDates[1];
                document.getElementById('start_date').value = flatpickr.formatDate(startDate, "d-m-Y");
                document.getElementById('end_date').value = flatpickr.formatDate(endDate, "d-m-Y");
            }
        }
    });
});