openssl aes-128-cbc -e -in plaint.txt -out cipher.bin -k "password" -nosalt
openssl aes-128-cbc -d -in cipher.bin -out plain.txt -k "password" -nosalt
openssl genrsa -out pvtkey.pem
openssl rsa -pubout -in pvtkey.pem -out pubkey.pem
>openssl rsa -text -in pvtkey.pem
openssl rsautl -encrypt -in plain.txt -pubin -inkey pubkey.pem -out c1.bin
openssl pkeyutl -decrypt -in c1.bin -inkey pvtkey.pem -out dec1.txt
openssl md5 plain.txt
openssl SHA256 plain.txt
openssl dgst -sha1 -sign pvtkey.pem -out s.bin plain.txt
openssl dgst -sha1 -verify pubkey.pem -signature s.bin plain.txt
Verified OK
