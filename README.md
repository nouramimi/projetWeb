open this :


![image](https://github.com/user-attachments/assets/3d0b09a2-ecda-4ecd-a987-7d1138169c06)


first of all clone the project git clone ...


hello to create a realm you ahve to run the fillowing command:


docker run -p 8181:8080 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:26.0.5 start-dev

and log to :
![image](https://github.com/user-attachments/assets/604bcbda-fdeb-40e5-97b0-1cb2ed7bcddd)

and yhen create a realm:
![image](https://github.com/user-attachments/assets/fbb61f6b-a1c0-44f1-853d-61726c84e260)

![image](https://github.com/user-attachments/assets/6acd2e7e-29e0-4c1d-ac3a-141cfe12d88c)

and then create a client:
![image](https://github.com/user-attachments/assets/fc84109c-1fbf-4044-b2cb-4d275d5168c0)
![image](https://github.com/user-attachments/assets/eadd5011-04e3-4041-8a2c-8fa5640528b3)

![image](https://github.com/user-attachments/assets/d76af1b5-bd00-4e0a-8d4a-79cca60467ab)
and then run the compose file using this command: docker compose up -d

here is how to get the auth token :

![image](https://github.com/user-attachments/assets/897132f7-a75a-45a1-9e3c-2ed7ace281b4)

in the client secret paste the client cridentials from this :
![image](https://github.com/user-attachments/assets/cb335d47-2679-45e3-87f2-fafdf1143d1b)

after getting the token paste it here after Bearer:

![image](https://github.com/user-attachments/assets/2d6f8be7-f3a8-49f7-bee0-1ae183ec45ac)

Here are the apis I tested:

![image](https://github.com/user-attachments/assets/f79ab96c-d40b-4879-bba9-db9e4502cdb2)

![image](https://github.com/user-attachments/assets/8927d8f3-2355-4e37-ad17-5d218333568f)


![image](https://github.com/user-attachments/assets/f898cc7a-be21-465f-b480-009a42c1f772)


to run kafka :

docker compose up -d
















