//alert("openInNewWindow - ${url}");

var dialog = top.WE_API.Common.createDialog();
dialog.setTitle('${title}');
dialog.addButton('OK', function(){
    dialog.hide();
    // top.WE_API.Common.execute("class:DoSomething", {param1:"Ich bin Nummer 1"},function(result) {alert(result);});
});

dialog.setSize(1200, 600);
elem = document.createElement('img');
elem.setAttribute('src', '${url}');
elem.setAttribute('width', '100%');
elem.setAttribute('height', '100%');
//elem.setAttribute('frameborder', 0);
dialog.setContent(elem);
dialog.show();
