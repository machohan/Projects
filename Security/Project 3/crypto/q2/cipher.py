#!/usr/bin/python3
import random
goodCharacters = ['\n', ' ', '!', '"', "'", '(', ')', ',', '-', '.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '?', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', ']', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z']

def encrypt(plaintext, key):
	random.seed(key)
	ciphertext = ""
	for j in range(len(plaintext)):
		c = plaintext[j]
		i = goodCharacters.index(c)
		i = (i + random.randrange(len(goodCharacters))) % len(goodCharacters)
		cp = goodCharacters[i]
		ciphertext = ciphertext + cp
	return ciphertext

def decrypt(ciphertext, key):
	random.seed(key)
	plaintext = ""
	for j in range(len(ciphertext)):
		c = ciphertext[j]
		i = goodCharacters.index(c)
		k = random.randrange(len(goodCharacters))
		i = (i - k) % len(goodCharacters)
		pt = goodCharacters[i]
		plaintext = plaintext + pt
	return plaintext


f = open("plain.txt", "r")
plaintext = f.read()
f.close()

# Decrypt the message Alice sent you below and send back an encrypted response.

# you need to figure out the shared, symmetric key as well as write the decrypt algorithm
key = 114510053174126045630315312560314578628207219096189975135427176266 # get this from the fixed dhke_bob.py and the last shared secret from sample_run.bash
ciphertext = encrypt(plaintext, key)
print(ciphertext)

f = open("cipher_response.txt", "r")
ciphertext = f.read()
f.close()

plaintext = decrypt(ciphertext, key)
print(plaintext)

