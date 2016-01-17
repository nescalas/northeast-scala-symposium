/* Utility JavaScript stuff. */

$(document).ready(function() {
  $(".section-icon").hide();

  $(".section").on("mouseover", function() {
    $(this).find(".section-icon").show();
  });

  $(".section").on("mouseout", function() {
    $(this).find(".section-icon").hide();
  });

});
