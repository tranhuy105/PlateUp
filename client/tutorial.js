document.addEventListener("DOMContentLoaded", (event) => {
  const popup = document.getElementById("tutorialPopup");
  const closeButton =
    document.querySelector(".close-button");

  popup.style.display = "flex";

  closeButton.addEventListener("click", () => {
    popup.style.display = "none";
  });
});
