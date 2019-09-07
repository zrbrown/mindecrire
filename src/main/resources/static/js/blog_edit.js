let tagContainer = document.getElementById("tagContainer");

function addTagToTags() {
    let tagName = document.getElementById("addTagInput").value;

    let tagSpan = document.createElement("span");
    tagSpan.setAttribute("class", "tag");
    tagSpan.appendChild(document.createTextNode(tagName));

    let tagRemoveLink = document.createElement("a");
    tagRemoveLink.setAttribute("href", "");
    tagRemoveLink.setAttribute("class", "tag tag-remove");
    tagRemoveLink.appendChild(document.createTextNode("ⓧ"));

    let tagWrapper = document.createElement("div");
    tagWrapper.setAttribute("class", "tag-wrapper");
    tagWrapper.appendChild(tagSpan);
    tagWrapper.appendChild(tagRemoveLink);

    tagContainer.appendChild(tagWrapper);

    let tagSubmitInput = document.createElement("input");
    tagSubmitInput.setAttribute("type", "hidden");
    tagSubmitInput.setAttribute("name", "addedTags");
    tagSubmitInput.setAttribute("value", tagName);

    tagContainer.appendChild(tagSubmitInput);
}

const uploadImageAjax = axios.create({
    baseURL: ajaxBaseUrl,
    timeout: 5000,
    headers: {
        "Content-Type": "multipart/form-data"
    }
});

const deleteImageAjax = axios.create({
    baseURL: ajaxBaseUrl,
    timeout: 5000
});

let uploadedImagesList = document.getElementById("uploadedImagesList");

function handleFileSelect(target) {
    let files = target.files;

    let formData = new FormData();
    let filenamesToFiles = {};
    for (let i = 0; i < files.length; i++) {
        let filename = files[i].name;
        let file = new Blob([files[i]]);

        filenamesToFiles[filename] = file;
        formData.append("files", file, filename);
    }

    uploadImages(formData, filenamesToFiles)
}

function handleCopyLinkClicked(target) {
    navigator.clipboard.writeText(target.getAttribute("data"))
        .catch(error => {
            uploadedImagesList.appendChild(error);
        });
}

function handleCancelFailedImageClicked(listItem) {
    uploadedImagesList.removeChild(listItem.parentElement);
}

function handleRetryImageUploadClicked(listItem, file, filename) {
    uploadedImagesList.removeChild(listItem);

    let formData = new FormData();
    formData.append("files", new Blob([file]), filename);

    let filenamesToFiles = {};
    filenamesToFiles[filename] = file;
    uploadImages(formData, filenamesToFiles);
}

function handleDeleteUploadedImageClicked(listItem, filename) {
    uploadedImagesList.removeChild(listItem);

    deleteImageAjax.delete("/content/image/delete/" + filename)
        .catch(error => {
            uploadedImagesList.appendChild(listItem);
        });
}

function uploadImages(formData, filenamesToFiles) {
    uploadImageAjax.post("/content/image/add", formData)
        .then(response => {
            for (let i = 0; i < response.data.successful.length; i++) {
                let successListItem = createSuccessfulUploadListItem(response.data.successful[i]);
                uploadedImagesList.appendChild(successListItem);
            }

            for (let i = 0; i < response.data.failed.length; i++) {
                let failedListItem = createFailedUploadListItem(response.data.failed[i],
                    filenamesToFiles[response.data.failed[i].filename]);
                uploadedImagesList.appendChild(failedListItem);
            }
        })
        .catch(error => {
            console.log(error);
        });
}

function createSuccessfulUploadListItem(successfulUpload) {
    let uploadedImageListItem = document.createElement("li");
    uploadedImageListItem.setAttribute("class", "file-link-list-item");

    let uploadedImageLink = createTextLink(successfulUpload.filename, "Click to copy", "file-link",
        () => handleCopyLinkClicked(uploadedImageLink));
    uploadedImageLink.setAttribute("data", successfulUpload.result);
    uploadedImageListItem.appendChild(uploadedImageLink);

    let deleteLink = createLink("Delete", "fa fa-trash file-action-icon",
        () => handleDeleteUploadedImageClicked(
            uploadedImageListItem,
            successfulUpload.filename));
    uploadedImageListItem.appendChild(deleteLink);

    return uploadedImageListItem;
}

function createFailedUploadListItem(failedUpload, failedUploadFile) {
    let failedImageListItem = document.createElement("li");
    failedImageListItem.setAttribute("class", "file-link-list-item");

    let uploadedImageLink = createTextLink(failedUpload.filename,
        failedUpload.result, "file-link failed-file-link", () => "");
    failedImageListItem.appendChild(uploadedImageLink);

    let cancelLink = createLink("Cancel", "fa fa-minus-circle file-action-icon",
        () => handleCancelFailedImageClicked(cancelLink));
    failedImageListItem.appendChild(cancelLink);

    if (failedUploadFile) {
        let retryLink = createLink("Retry", "fa fa-refresh file-action-icon",
            () => handleRetryImageUploadClicked(
                failedImageListItem,
                failedUploadFile,
                failedUpload.filename));
        failedImageListItem.appendChild(retryLink);
    }

    return failedImageListItem;
}

function createLink(title, styleClass, callback) {
    let link = document.createElement("a");
    link.setAttribute("class", styleClass);
    link.setAttribute("title", title);
    link.setAttribute("href", "javascript:void(0);");
    link.addEventListener("click", callback);

    return link;
}

function createTextLink(text, title, styleClass, callback) {
    let link = createLink(title, styleClass, callback);
    link.appendChild(document.createTextNode(text));

    return link;
}