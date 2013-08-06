var colors = [
  [178, 209, 240],    // Blue
  [136, 198, 186],    // Green
  [254, 229, 149], // yellow
  [254, 174, 150], // orange
  [243, 111, 116],    // Red
  // [178, 103, 144]    // random
];

var height = 0;
var isChrome = window.chrome;

$(document).on('ready', function(){
	height = $('.content-wrapper').height() - window.innerHeight;
	if(isChrome) {
		$('#add-chrome-ext').removeClass('hide');
	}
});

$(document).scroll(function() {
   var body = $('.content-wrapper');
   var steps = Math.floor(height / colors.length);
   var position = $(this).scrollTop();
   var currentStep = Math.floor(position / steps);
   if ( currentStep === colors.length ) currentStep = colors.length - 1;
   var rgb = $('.content-wrapper').css('background-color').replace('rgb(','').replace(')','').replace(/\s/g, '').split(',');     
   var previousColor = colors[currentStep] || colors[0];
   var nextColor = colors[currentStep+1] || colors[colors.length-1];
   var percentFromThisStep = ( position - ( currentStep * steps ) ) / steps;
   if ( percentFromThisStep > 1 ) percentFromThisStep = 1;
  
   var red = Math.floor(previousColor[0] + ( ( nextColor[0] - previousColor[0] ) * percentFromThisStep ));
   var green = Math.floor(previousColor[1] + ( ( nextColor[1] - previousColor[1] ) * percentFromThisStep ));
   var blue = Math.floor(previousColor[2] + ( ( nextColor[2] - previousColor[2] ) * percentFromThisStep )); 
   var newRgb = [
      red,
      green,
      blue
   ];
   
   $('.content-wrapper').css('background-color', 'rgb('+ newRgb.join(',') +')');

});