# BookMyRoom — HTML, CSS and plain JavaScript edition

No React, npm, Node.js, Vite or frontend build step. Spring Boot serves the entire application at **http://localhost:8080/**.

## Start
1. Open the `BookMyRoom` folder (the one containing `pom.xml`) in IntelliJ.
2. Ensure MySQL is running and the `bookmyroom` database exists. Set `DB_PASSWORD` in IntelliJ's Run Configuration to your MySQL password. Never commit a real password.
3. Run `BookmyroomApplication.java` in IntelliJ, or open PowerShell in this folder and run `mvn spring-boot:run` (with `DB_PASSWORD` set in the environment).
4. Open **http://localhost:8080/** in your browser. That's it; there is no `npm install`.
5. Run `mvn test` to execute backend tests.

## Included screens
Dashboard; Rooms (view, add, edit); Guests (view, add, edit); Bookings (create, view, cancel, complete on/after checkout); Monthly Reports. No DELETE buttons.

## Folder layout
- `src/main/java` — existing Spring Boot backend.
- `src/main/resources/static/index.html` — HTML frontend.
- `src/main/resources/static/styles.css` — CSS design.
- `src/main/resources/static/app.js` — plain JavaScript, `fetch()` requests to `/api/...`.

## Viva explanation
HTML builds the page, CSS styles it, JavaScript loads data and submits forms using `fetch()`, Spring Boot validates requests and talks to MySQL, then sends JSON back. Since Spring Boot serves the HTML and API on the same port, there is no CORS configuration or separate frontend server needed.

## Notes
This is a college/demo application without user authentication. Do not expose it directly to the public internet. Monthly reports use the current room count for historical months. Automated tests may connect to your configured MySQL database; use a separate test database for any destructive tests.
