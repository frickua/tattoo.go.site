// Magnific popup calls
  $('.popup-gallery').magnificPopup({
    delegate: 'a',
    type: 'image',
    tLoading: 'Loading image #%curr%...',
       mainClass: 'mfp-with-zoom',

        zoom: {
           enabled: true, // By default it's false, so don't forget to enable it

           duration: 300, // duration of the effect, in milliseconds
           easing: 'ease-in-out', // CSS transition easing function

           // The "opener" function should return the element from which popup will be zoomed in
           // and to which popup will be scaled down
           // By defailt it looks for an image tag:
           opener: function(openerElement) {
             // openerElement is the element on which popup was initialized, in this case its <a> tag
             // you don't need to add "opener" option if this code matches your needs, it's defailt one.
             return openerElement.is('img') ? openerElement : openerElement.find('img');
           }
         },
    gallery: {
      enabled: true,
      navigateByImgClick: true,
      preload: [0, 1]
    },
    image: {
      tError: '<a href="%url%">The image #%curr%</a> could not be loaded.',
      titleSrc: function(item) {
            function isBlank(str) {
              return str == null || str == undefined || str === '';
            }
            title = item.el.attr('title');
            style = item.el.attr('style');
            price = item.el.attr('price');
            author = item.el.attr('author');
            if (!isBlank(title) && !isBlank(style)) {
              title = title + ' # ' + style;
            } else if (!isBlank(style)) {
              title = style;
            } else if (isBlank(title)) {
              title = '';
            }
      			return title + '<div>' +
      		'<small style="float:left;">' + (price == undefined || price == 0 ? 'Цену уточняйте' :  'Набъем за: ' + price +' грн') + '</small>' +
      		(isBlank(author) ? '' : '<small style="float:right;"><i>Мастер: ' + item.el.attr('author') +'</i></small>')+
      		'</div>';
      }
    }
  });