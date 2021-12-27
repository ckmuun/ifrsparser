'use strict';

var fileUploadForm = document.querySelector('#fileUploadForm')
var fileUploadFormInput = document.querySelector('#fileUploadFormInput')
var fileUploadError = document.querySelector('#fileUploadError')
var fileUploadSuccess = document.querySelector('#fileUploadSuccess')


function uploadFile(file) {
    console.log("uploading file")
    const formData = new FormData();
    formData.append("file", file);

    const xhr = new XMLHttpRequest();
    xhr.open("POST", "/api/v1/parse-ifrs")


    xhr.onload = function () {
        console.log(xhr.responseText)
        const response = JSON.parse(xhr.responseText)

        if(xhr.status == 200) {
            fileUploadError.style.display = "none";
            fileUploadSuccess.innerHTML = "success"
        } else {
            fileUploadSuccess.style.display = "none";
            fileUploadError.innerHTML = "an error occured";
        }
    };
    xhr.send(formData)
}

fileUploadForm.addEventListener('submit', function (event) {
    const file = fileUploadFormInput.file;

    uploadFile(file);
    event.preventDefault();
}, true)
