# RegX

Reguláris kifejezéseket feldolgozó nyelv.

## Típusok

A nyelv háromféle típust támogat, melyek a következőek:

  * `string`: Karaktersorozat
  * `list`: Lista, melynek elemei `string` típusúak
  * `regex`: Reguláris kifejezés

## Változódeklarációk

Változót deklarálni a következő módon tudunk:

~~~~
  típus azonosító [ = kezdőérték];
~~~~

Az azonosító az angol ABC kis- és nagybetűivel, vagy a `$` és `_` karakterekkel kezdődhet és ugyanezekkel, valamint számokkal áfolytatódhat.

~~~~
  // szabályos
  string $str1;

  // szabálytalan
  string 12a;
~~~~

Példa különféle típusú változók deklarálására és inicializálására:

~~~~
  string str = "Hello, World!";

  list lst = ["Hello", "World", "!"];

  regex rx = /a+b*/;
~~~~

Az egyenlőségjel jobboldalán tetszőleges, a megfeleő típust visszaadó kifejezés, így függvényhívás is szerepelhet. `list` esetén a szögletes zárójelek közt csak `string` literálok sorolhatók fel. 

`regex` típus esetén, ahogy a példában is látható, a reguláris kifejezést `/` jelek közé kell zárni, ahogy JavaScriptben.

Egyszerre csak egy változó deklarálására van lehetőség, a következő nem megengedett:

~~~~
  string a, b, c;
~~~~

### Kezdőérték

Ha egy típust nem inicializálunk, akkor a változó kezdőértéke

  * `string` esetén az üres `string` azaz `""` lesz,
  * `list` esetén az üres lista, azaz `[]` lesz,
  * `regex` esetén az üres kiefejezés, azaz `//` lesz.

## Értékadás

Változóknak új értéket adhatunk, ha az `=` baloldalán adjuk meg őket:

~~~~
  string str;

  str = "abc";

  str = "acde";
~~~~ 

Természetesen függvényhívást is használhatunk. Lehetőség van arra is, hogy egyidőben több változót is azonos értékre állítsunk be, a következő módon:

~~~~
  string a;
  string b;

  a = b = "Hello!";
~~~~

Ilyenkor a legjobboldalibb kifejezés kerül először kiértékelésre, majd a kapott értéket jobbról balra haladva elhelyezzük a felsorolt változókban. 

## For ciklus

Egy speciális `for` ciklus használható a nyelvben, mely egy `list` típusú változó elemein képes végigiterálni. Ehhez meg kell adnunk egy ciklusváltozót, valamint egy `list` típusú változót:

~~~~
  for (e : lst) {
    // ciklusmag
  }
~~~~

Természetesen, ha üres a lista, a ciklusmag egyszer sem hajtódik végre. Egyéb esetben a ciklusváltozó (a példában `e`) sorban felveszi a lista elemeinek értékét, és rendelkezésre áll a ciklusmagban.

Amennyiben a ciklusmag egyetlen utasításból áll, elhagyhatjuk a kapcsos zárójeleket:

~~~~
  for (e : lst)
    print(e);
~~~~

## Függvények

Függvényt általánosan a következőképpen tudunk deklarálni:

~~~~
  function visszatérési_érték azonosító([paraméterek])
    törzs
~~~~

Mindeképpen a `function` kulcsszóval kell tehát kezdenünk a függvények deklarációját. Ezt követi a visszatérési érték. Olyan függvények esetén, melyek nem adnak vissza értéket, a `void` értéket kell itt megadnunk.

Az azonosító lesz a függvény neve, amivel majd meg tudjuk hívni. Erre ugyanazok a szabályok vonatkoznak, mint a változók azonosítóira. 

### Paraméterlista

A függvény formális paraméterlistáját (amennyiben elfogad paramétereket) zárójelek között kell megadnunk. Ebben a formális paramétereket vesszővel elválasztva lehet felsorolni:

~~~~
  function void f(string a, list b, regex c) {
    törzs
  }
~~~~

### Törzs

A függvény törzsét kapcsos zárójelek között adhatjuk meg, melyek azonban elhagyhatóak, ha a függvény mindössze egyetlen utasításból áll. Például:

~~~~
  function void prnt(string str)
    print(str);
~~~~ 

### Visszatérési érték

Ha a függvény visszatérési értéke nem `void`, akkor a `return` segítségével térhetünk vissza a függvényből valamilyen értékkel:

~~~~
  function string id(string str) {
    return str;
  }
~~~~

A visszaadott érték típusának meg kell egyeznie a függvény szignatúrájában meghatározott típussal.

Ha a függvény visszaad valamilyen értéket, akkor nem lehet olyan végrehajtási ág, amelynek futása ne egy `return` utasítással érne véget. Például:

~~~~
  function string a() { <- helytelen
    for (e : []) 
      return "str";
  } 

  function string b() { <- helyes
    for (e : []) 
      return "str";

    return "abc";
  }
~~~~

### main

Ahhoz, hogy a programunk futtatható legyen, szükség van egy `main` függvényre, melynek a szignatúrája a következő kell, hogy legyen:

~~~~
  function void main() {
    főprogram
  }
~~~~

Tehát a visszatérési értéke `void` és nem vár paramétereket.

## Hatáskör

Egy változó abban a blokkban (vagy annak gyermekblokkjaiban) lesz látható, ahol deklarálásra került, és csak a deklarációt követően. Nincsen *forward declaration*, egy változó vagy függvény csak az utána deklarált változók, függvények számára látható.

A különböző blokkokban deklarált, azonos nevű változók elfedik egymást. Például:

~~~~
  string str;

  function void a() {
    string str; <- elfedi a külső str-t
  }
~~~~

Azonban azonos blokkon belül nem lehet két azonos nevű változót deklarálni:

~~~~
  function void a() {
    string str;

    string str; <- Hiba! Már van ilyen nevű változó ebben a blokkban.
  }
~~~~

Új blokkot a függvények és a `for` ciklusok nyitnak. Más módon nincs lehetőségünk új blokk létrehozására.

A hatáskörök ilyen módon való kezelése maga után vonja azt is, hogy bármilyen változó vagy függvény, ami a `main` után kerül deklarálásra, használhatatlan lesz a főprogramban, nem kerül kiértékelésre.

## Beépített eszközök

A futtató környezet biztosít különböző meghívható függvényeket a felhasználók számára,
hogy megkönnyítse a reguláris kifejezésekkel és a sztringekkel való munkát.

### Sztringek és reguláris kifejezések kiíratása

Alapvető beépített függvény a `print`, mely `string` típusú változókat képes az outputra továbbítani.
A `regex` típusokat előbb szöveggé kell konvertálni - erre szolgál az `as_text` függvény-,
csak ezt követően lehet a `print` függvényt alkalmazni. Példaprogram:

~~~~
  regex a = /ab*+cd/;
  string s = "alma";

  print(s);
  print(as_text(a));
~~~~

### Véges automata generálása

A nyelvtan által felírható reguláris kifejezésekhez lehet véges automatát generálni, mely
nemdeterminisztikus, de a reguláris kifejezéssel ekvivalens. Példaprogram:

~~~~
  regex r = /(a+b)*/;

  print_automaton(r);
~~~~

Amelynek kimenete:

~~~~
  Automaton:
  --------
  2 | \ | 3
  --------
  3 | a | 4
  --------
  2 | \ | 5
  --------
  5 | b | 6
  --------
  4 | \ | 7
  --------
  6 | \ | 7
  --------
  7 | \ | 2
  --------
  1 | \ | 2
  --------
  7 | \ | 8
  --------
  1 | \ | 8
  --------
  Starting state: 1
  Accepting states: 8
~~~~

### Unió normálforma

A reguláris kifejezéseket beépített függvény segítségével unió normálformára is hozhatjuk.

Ehhez a négy fő átalakítási szabályt alkalmazza a függvény:
  1. `(p+r)* -> (p*r*)*`
  1. `p(q+r) -> pq+pr`
  1. `(p+q)r -> pr+qr`
  1. `(p+q)(r+t) -> pr+pt+qr+qt`

Példaprogram:

~~~~
  regex a = /(a+b)*(c+d+e)/;
  regex b = normalize(a);
  
  print(as_text(b));
~~~~

### Egyszerűsítés

A reguláris kifejezéseket bizonyos szabályok alapján egyszerűsíteni is lehet.

Ilyen átalakítások lehetnek:
  1. Fölösleges zárójelek elhagyása: `(a) -> a és ((a+b))* -> (a+b)*`
  1. Többszörös `*` használata: `(a*)* -> a*`
  1. Azonos komponensek egy unión beül: `a+b+a -> a+b`

~~~~
  regex a = /(a)+(c*)*+bd+a+c*/;
  regex s = simplify(a);
  
  print(as_text(s));
~~~~

Amelynek kimenete:
~~~~
  a+c*+bd
~~~~

### Szóprobléma

A `match` függvény segítségével meghatározhatjuk, hogy egy adott `string` illeszkedik-e egy megadott reguláris kifejezésre. Az eredményt a futtató környezet a standard kimenetre írja:

~~~~
  match(/abc/, "abc"); <- true

  match(/def/, "abc"); <- false
~~~~

### Helyettesítés

Egy karaktert egy reguláris kifejezésen belül egy másik reguláris kifejezéssel a `subsitute` függvénnyel tudunk helyettesíteni. Ez a megadott karakter minden előfordulását kicseréli a megadott kifejezésre. A függvény a módosított reguláris kifejezéssel tér vissza.

~~~~
  regex s = /ab/;

  regex r = substitute(s, "a", /a*b/);

  print(as_text(r)); <- a*bb
~~~~

### Listaműveletek

Elhelyezhetünk új `string` típusú element a egy lista elejére a `push_front` beépített függvény segítségével:

~~~~
  list lst = [];

  push_front(lst, "Hello");
~~~~

Ennek a párja a `push_back`, mely a lista végére helyezi el a megadott `string` értéket.

A listából való törlésre a `pop_front` és a `pop_back` függvények szolgálnak, melyek a lista első, vagy utolsó elemét távolítják el. Az eltávolított elemet mindkét függvény visszaadja.

~~~~
  list lst = ["Hello", "World"];

  string s = pop_front(lst);
~~~~

### String műveletek

`string` értékek összefűzésére szolgál a `concat` függvény, mely az első `string`hez hozzáfűzi a másodikat, és visszaadja az így előállított karaktersorozatot.

~~~~
  string s = concat("Hello", " World!");

  print(s); <- "Hello World!" 
~~~~

Egy `string` érték valamilyen elválasztó karaktersorozat szerinti felbontására használható az `explode` függvény, mely egy listát ad vissza, mely az elválasztott karaktersorozatokat tartalmazza:

~~~~
  list lst = explode("abc,def,ghi", ","); <- ["abc", "def", "ghi"];
~~~~