<!DOCTYPE HTML>
<html lang="ru">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous" />
    <title>car sale</title>

    <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
    <script src="https://code.jquery.com/jquery-3.4.1.min.js" ></script>
</head>
<body>
<div class="container">
    <div class="container">
        <ul class="nav">
            <li class="nav-item">
                <a class="nav-link" href="edit.html" >Добавить объявление</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="login.html">Войти</a>
            </li>
        </ul>
    </div>
    <div class="container">
        <table id="table" class="table table-bordered">
            <thead id="thead">
            <tr>
                <th style="width: 25%;">Фото</th>
                <th style="width: 15%;">Авто</th>
                <th style="width: 30%;">Описание</th>
                <th style="width: 10%;">Количество владельцев</th>
                <th style="width: 10%;">Год выпуска</th>
                <th style="width: 10%;">Статус</th>
            </tr>
            </thead>
            <tbody id="tablebody">
            </tbody>
        </table>
    </div>
</div>
<script>
    function loadAdvertisement() {
        $('#thead').remove();
        $('#tablebody').remove();
        $('#table').append(
            '<thead id="thead">' +
            '<tr>' +
            '<th style="width: 25%;">Фото</th>' +
            '<th style="width: 15%;">Авто</th>' +
            '<th style="width: 30%;">Описание</th>' +
            '<th style="width: 10%;">Количество владельцев</th>' +
            '<th style="width: 10%;">Год выпуска</th>' +
            '<th style="width: 10%;">Статус</th>' +
            '</tr>' +
            '</thead>' +
            '<tbody id="tablebody">' +
            '</tbody>'
        );
        $.ajax({
            type: 'GET',
            url: 'http://localhost:8080/carsale/advertisement',
            dataType: 'text'
        }).done(function(data) {
            const advertisements = JSON.parse(data);
            const userUUId = sessionStorage.getItem("userUUID");
            for (let i = 0; i < advertisements.length; ++i) {
                const advertisement = advertisements[i];
                $('#tablebody').append(
                    '<tr><td tyle="width: 25%;">' +
                    '<img src="img?id=' + advertisement.photo + '" width="250px" height="250px" />' + '</td>' +
                    '<td tyle="width: 15%;">' + advertisement.model.brand.name + ' ' +
                    advertisement.model.description + ' ' + advertisement.transmission.name + '</td>'  +
                    '<td tyle="width: 30%;">' + advertisement.description + '</td>' +
                    '<td tyle="width: 10%;">' + advertisement.owners + '</td>' +
                    '<td tyle="width: 10%;">' + advertisement.yearIssue + '</td>'  +
                    '<td id="statusField" tyle="width: 10%;">' + '<div id="status">' + advertisement.status.description + '</div>' +
                    ((userUUId == advertisement.user.id) ?
                        '<br><div><a href="edit.html?id=' + advertisement.id +'">Редактировать</a><div>'
                        : '')
                    + '</td>' + '</tr>'
                );
            }
        }).fail(function(err){
            alert(err.toSource);
        });
    }
    loadAdvertisement();
</script>
</body>
</html>