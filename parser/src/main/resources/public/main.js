'use strict';

var singleUploadForm = document.querySelector('#singleUploadForm');
var singleFileUploadInput = document.querySelector('#singleFileUploadInput');
var singleFileUploadError = document.querySelector('#singleFileUploadError');
var singleFileUploadSuccess = document.querySelector('#singleFileUploadSuccess');

var multipleUploadForm = document.querySelector('#multipleUploadForm');
var multipleFileUploadInput = document.querySelector('#multipleFileUploadInput');
var multipleFileUploadError = document.querySelector('#multipleFileUploadError');
var multipleFileUploadSuccess = document.querySelector('#multipleFileUploadSuccess');


/*
    ADAPTED FROM https://github.com/callicoder/spring-boot-file-upload-download-rest-api-example
 */

function uploadSingleFile(file) {
    var formData = new FormData();
    formData.append("file", file);

    var xhr = new XMLHttpRequest();
    xhr.responseType = "blob"
    xhr.open("POST", "/api/v1/parse-ifrs");

    xhr.onload = function () {
        console.log(xhr.responseText);
        //  const response = JSON.parse(xhr.responseText);
        if (xhr.status == 200) {
            singleFileUploadError.style.display = "none";
            singleFileUploadSuccess.innerHTML = "<p>File Uploaded Successfully.</p>"
            singleFileUploadSuccess.style.display = "block";

          //  download("parsed.xlsx", xhr.response)
            _html5Saver(xhr.responseXML, 'parsed1.xlsx')

        } else {
            singleFileUploadSuccess.style.display = "none";
            //           singleFileUploadError.innerHTML = (response && response.message) || "Some Error Occurred";
        }
    }
    xhr.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            var content = xhr.response;
            _html5Saver(content, 'parsed.xlsx');
        }
    };


    xhr.send(formData);
}


// adapted from stackoverflow
function _html5Saver(blob, fileName) {
    // to emulate click action
    // because we cannot save directly to client's computer due to security constraints
    var a = document.createElement("a");
    document.body.appendChild(a);
    a.style = "display: none";
    var url = window.URL.createObjectURL(blob);
    a.href = url;
    a.download = fileName;
    a.click();

    document.body.removeChild(a);
}


function uploadMultipleFiles(files) {
    var formData = new FormData();
    for (var index = 0; index < files.length; index++) {
        formData.append("files", files[index]);
    }

    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/api/v1/parse-ifrs");

    xhr.onload = function () {
        console.log(xhr.responseText);
        var response = JSON.parse(xhr.responseText);
        if (xhr.status == 200) {
            multipleFileUploadError.style.display = "none";
            var content = "<p>All Files Uploaded Successfully</p>";
            // for(var i = 0; i < response.length; i++) {
            //     content += "<p>DownloadUrl : <a href='" + response[i].fileDownloadUri + "' target='_blank'>" + response[i].fileDownloadUri + "</a></p>";
            // }
            multipleFileUploadSuccess.innerHTML = content;
            multipleFileUploadSuccess.style.display = "block";
        } else {
            multipleFileUploadSuccess.style.display = "none";
            multipleFileUploadError.innerHTML = (response && response.message) || "Some Error Occurred";
        }
    }

    xhr.send(formData);
}

singleUploadForm.addEventListener('submit', function (event) {
    var files = singleFileUploadInput.files;
    if (files.length === 0) {
        singleFileUploadError.innerHTML = "Please select a file";
        singleFileUploadError.style.display = "block";
    }
    uploadSingleFile(files[0]);
    event.preventDefault();
}, true);


multipleUploadForm.addEventListener('submit', function (event) {
    var files = multipleFileUploadInput.files;
    if (files.length === 0) {
        multipleFileUploadError.innerHTML = "Please select at least one file";
        multipleFileUploadError.style.display = "block";
    }
    uploadMultipleFiles(files);
    event.preventDefault();
}, true);

