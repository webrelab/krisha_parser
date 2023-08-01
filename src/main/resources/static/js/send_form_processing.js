document.getElementById('submit-button').addEventListener('click', function() {
    event.preventDefault();
    // Показать индикатор загрузки
    document.getElementById('loading').style.display = 'block';
    document.getElementById('donate').style.display = 'block';
    document.getElementById('donut-image').style.display = 'block';


    // Получить форму
    let form = document.getElementById('property-form');
    document.getElementById("property-form").style.display = 'none'

    // Создать объект FormData из формы
    let formData = new FormData(form);

    // Отправить данные формы на сервер
    fetch('/startProcessing', {
        method: 'POST',
        body: formData
    })
        .then(response => response.text())
        .then(fileId => {
            // Скрыть индикатор загрузки
            document.getElementById('loading').style.display = 'none';
            document.getElementById("property-form").style.display = 'block'

            // Перенаправить на URL скачивания файла
            window.location.href = fileId;
        });
});
