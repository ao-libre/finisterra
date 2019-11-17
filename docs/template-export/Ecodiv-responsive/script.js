$(function(){
        $('.toggler').click(function(){
                $('#list').fadeToggle("fast");
            });
        });


$(function() {
 
    // grab the initial top offset of the navigation
    var sticky_navigation_offset_top = $('#taskbar').offset().top;
     
    // our function that decides weather the navigation bar should have "fixed" css position or not.
    var sticky_navigation = function(){
        var scroll_top = $(window).scrollTop(); // our current vertical position from the top
         
        // if we've scrolled more than the navigation, change its position to fixed to stick to top,
        // otherwise change it back to relative
        if (scroll_top > sticky_navigation_offset_top) {
            $('#sticky_taskbar').css({ 'position': 'fixed', 'top':0, 'left':0 });
        } else {
            $('#sticky_taskbar').css({ 'position': 'relative' });
        }  
    };
     
    // run our function on load
    sticky_navigation();
     
    // and run it again every time you scroll
    $(window).scroll(function() {
         sticky_navigation();
    });
 
});
