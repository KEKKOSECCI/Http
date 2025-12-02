let count = 0;
        const button = document.getElementById('segheButton');
        const counter = document.getElementById('counter');

        button.addEventListener('click', function() {
            count++;
            counter.textContent = count;
        });