// Global variable to store issued books data
var allRecords = [];

// Global Initialization
document.addEventListener('DOMContentLoaded', () => {
    const path = window.location.pathname;
    var page = path.split('/').pop().split('?')[0].toLowerCase();

    switch (page) {
        case 'login.html':
        case 'register.html':
            handleAuthAlerts();
            break;
        case 'dashboard.html':
            initAdminDashboard();
            break;
        case 'issuebook.html':
            initIssueBookForm();
            break;
        case 'booklist.html':
            renderNavbar('books');
            initBooksPage();
            break;
        case 'memberlist.html':
            renderNavbar('members');
            initMembersPage();
            break;
        case 'issuedbooklist.html':
            renderNavbar('issued');
            initIssuedBooksPage();
            break;
        case 'userdashboard.html':
            renderNavbar('dashboard');
            break;
    }

    handleGlobalToasts();
});

function handleGlobalToasts() {
    const urlParams = new URLSearchParams(window.location.search);
    const msg = urlParams.get('msg');
    const toastError = urlParams.get('error');

    if (msg === 'issued') showToast('Book issued successfully');
    if (msg === 'returned') showToast('Book returned successfully');
    if (msg === 'saved') showToast('Settings saved');
    if (msg === 'deleted') showToast('Deleted successfully');

    if (toastError === 'invalid') showToast('Invalid input provided', true);
    if (toastError === 'failed') showToast('Operation failed', true);
}

// AUTH ALERTS: Shows error or success messages from the URL
function handleAuthAlerts() {
    var urlParams = new URLSearchParams(window.location.search);
    var error = urlParams.get('error');
    var success = urlParams.get('success');
    var alertContainer = document.getElementById('alert-container');

    if (alertContainer == null) {
        return;
    }

    if (error != null) {
        var message = "An error occurred.";
        if (error == 'invalid_credentials') {
            message = "Invalid email or password.";
        } else if (error == 'empty_fields') {
            message = "Please fill in all fields.";
        } else if (error == 'failed') {
            message = "Operation failed. Try again.";
        }

        alertContainer.innerHTML = '<div class="lib-alert lib-alert-error">' +
            '<i class="fas fa-exclamation-circle"></i> ' +
            '<span>' + message + '</span>' +
            '</div>';
    } else if (success == 'registered') {
        alertContainer.innerHTML = '<div class="lib-alert lib-alert-success">' +
            '<i class="fas fa-check-circle"></i> ' +
            '<span>Registration successful! Please sign in.</span>' +
            '</div>';
    }
}

// ADMIN DASHBOARD
// // ADMIN DASHBOARD: Loads numbers and recent activity
function initAdminDashboard() {
    var activityList = document.getElementById('activityList');
    if (activityList == null) return;

    fetch('DashboardDataServlet')
        .then(function (response) {
            return response.json();
        })
        .then(function (data) {
            // 1. Update the top count numbers
            if (document.getElementById('totalBooks')) document.getElementById('totalBooks').textContent = data.totalBooks;
            if (document.getElementById('totalMembers')) document.getElementById('totalMembers').textContent = data.totalMembers;
            if (document.getElementById('issuedBooks')) document.getElementById('issuedBooks').textContent = data.issuedBooks;
            if (document.getElementById('overdueBooks')) document.getElementById('overdueBooks').textContent = data.overdueBooks;

            // 2. Show Overdue Books List (No Badge)
            var overdueList = document.getElementById('overdueList');
            if (overdueList != null) {
                var listHtml = "";
                for (var i = 0; i < data.overdueBooksList.length; i++) {
                    var b = data.overdueBooksList[i];
                    listHtml += '<div class="overdue-item">' +
                        '<div><strong class="text-primary">' + b.title + '</strong><br>' +
                        '<small class="text-muted">Member: ' + b.memberName + '</small></div>' +
                        '<span class="font-bold">' + b.daysOverdue + ' days overdue</span>' +
                        '</div>';
                }
                if (listHtml == "") listHtml = "<p class='text-muted p-3'>No overdue books</p>";
                overdueList.innerHTML = listHtml;
            }

            // 3. Show Recently Added Books (No ID column content)
            var tbody = document.getElementById('recentBooksTable');
            if (tbody != null) {
                var rows = "";
                for (var i = 0; i < data.recentBooks.length; i++) {
                    var b = data.recentBooks[i];
                    rows += '<tr>';
                    rows += '<td>-</td>'; // Removing Book ID
                    rows += '<td><div class="font-bold text-primary">' + b.title + '</div></td>';
                    rows += '<td>' + b.author + '</td>';
                    rows += '<td>' + b.category + '</td>';
                    rows += '<td class="font-bold">' + b.quantity + '</td>';
                    rows += '</tr>';
                }
                if (rows == "") rows = "<tr><td colspan='5'>No recent books</td></tr>";
                tbody.innerHTML = rows;
            }

            // 4. Show Recent Activity (Simple status)
            if (activityList != null) {
                activityList.innerHTML = '<div class="activity-item">' +
                    '<div class="stat-orb orb-yellow orb-sm"><i class="fas fa-check-circle"></i></div>' +
                    '<div><p class="text-bold">System Status</p><small class="text-muted">All data is up to date</small></div>' +
                    '</div>';
            }
        });
}

// ISSUE BOOK FORM
function initIssueBookForm() {
    const issueDateEl = document.getElementById('issue');
    const returnDateEl = document.getElementById('return');
    const form = document.querySelector('form');

    if (issueDateEl) issueDateEl.valueAsDate = new Date();

    if (form && issueDateEl && returnDateEl) {
        form.addEventListener('submit', function (e) {
            const issue = new Date(issueDateEl.value);
            const ret = new Date(returnDateEl.value);
            if (ret <= issue) {
                e.preventDefault();
                alert('Return date must be after the issue date.');
            }
        });
    }
}

// BOOKS PAGE
function initBooksPage() {
    const role = localStorage.getItem('userRole') || 'user';
    const adminActions = document.getElementById('adminActions');
    if (adminActions && role === 'admin') {
        adminActions.style.display = 'block';
    }
    fetchBooks();
}

// BOOKS: Fetches the list of books and shows them in a table
function fetchBooks() {
    var tbody = document.getElementById('booksTableBody');
    if (tbody == null) return;

    fetch('view')
        .then(function (response) {
            return response.json();
        })
        .then(function (data) {
            var rows = "";
            for (var i = 0; i < data.length; i++) {
                var b = data[i];
                var role = localStorage.getItem('userRole');
                
                rows += '<tr>';
                // 1. Title and Author (No ID, No Image)
                rows += '<td class="p-1 px-4">';
                rows += '<div class="font-black text-primary fs-md mb-0">' + b.title + '</div>';
                rows += '<div class="fs-sm text-muted">' + b.author + '</div>';
                rows += '</td>';
                
                // 2. Category
                rows += '<td class="font-bold">' + b.category + '</td>';
                
                // 3. Availability (No Color Badge)
                rows += '<td class="font-bold text-primary fs-md">' + b.quantity + '</td>';
                
                // 4. Action (Premium Buttons)
                if (role == 'admin') {
                    rows += '<td>';
                    rows += '<a href="edit?id=' + b.id + '" class="lib-btn lib-btn-secondary h-32 px-3 fs-sm">Edit</a>';
                    rows += '</td>';
                } else {
                    rows += '<td><a href="userissuebook.html?bid=' + b.id + '" class="lib-btn lib-btn-primary h-32 px-3 fs-sm">Issue</a></td>';
                }
                rows += '</tr>';
            }
            tbody.innerHTML = rows;
        });
}

function deleteBook(id, title) {
    if (!confirm(`Are you sure you want to delete "${title}"?`)) return;

    const formData = new URLSearchParams();
    formData.append('bookId', id);

    fetch('DeleteBook', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData
    }).then(res => {
        if (res.redirected || res.ok) {
            showToast('Book deleted successfully');
            fetchBooks();
        } else {
            showToast('Failed to delete book', true);
        }
    });
}

// MEMBERS PAGE
function initMembersPage() {
    fetchMembers();
}

// MEMBERS: Fetches the list of members
function fetchMembers() {
    var tbody = document.getElementById('membersTableBody');
    if (tbody == null) return;

    fetch('viewmember')
        .then(function (response) {
            return response.json();
        })
        .then(function (data) {
            var rows = "";
            for (var i = 0; i < data.length; i++) {
                var m = data[i];
                rows += '<tr>';
                rows += '<td class="p-1 px-4">';
                rows += '<div class="font-black text-primary fs-md">' + m.name + '</div>';
                rows += '<div class="fs-sm text-muted">ID: MEM-' + m.id + '</div>';
                rows += '</td>';
                rows += '<td><b>' + m.email + '</b><br><small>' + m.phone + '</small></td>';
                rows += '<td><span class="badge">' + m.role + '</span></td>';
                rows += '<td><a href="editmember?id=' + m.id + '" class="lib-btn lib-btn-secondary h-32 px-3 fs-sm">Edit</a></td>';
                rows += '</tr>';
            }
            tbody.innerHTML = rows;
        });
}

// ISSUED BOOKS PAGE
function initIssuedBooksPage() {
    fetchIssued();
}

// ISSUED BOOKS: Fetches the issued records and shows them in a table
function fetchIssued() {
    var tbody = document.getElementById('issuedTableBody');
    if (tbody == null) return;

    fetch('IssuedBook')
        .then(function (response) {
            return response.json();
        })
        .then(function (data) {
            allRecords = data; // Store data for the popup
            var rows = "";
            for (var i = 0; i < data.length; i++) {
                var r = data[i];
                var statusText = r.returned ? "Returned" : "Active";

                rows += '<tr>';
                // 1. Book Info (No ID)
                rows += '<td class="p-1 px-4"><div class="font-black text-primary fs-md">' + r.bookTitle + '</div></td>';
                
                // 2. Member Info (No ID)
                rows += '<td><div class="font-bold">' + r.memberName + '</div></td>';
                
                // 3. Dates
                rows += '<td><div class="fs-sm">' + r.issueDate + '</div><div class="fs-xs text-muted">to ' + r.returnDate + '</div></td>';
                
                // 4. Status (No Color Badge)
                rows += '<td class="font-bold">' + statusText + '</td>';
                
                // 5. Action (Premium Buttons)
                rows += '<td>';
                rows += '<button onclick="showDetails(' + r.id + ')" class="lib-btn lib-btn-secondary h-32 px-3 fs-sm">Details</button>';
                rows += '</td>';
                
                rows += '</tr>';
            }
            tbody.innerHTML = rows;
        });
}

// SHOW DETAILS: Shows the book details in a popup (modal)
function showDetails(id) {
    // 1. Find the record in our data list
    var r = null;
    for (var i = 0; i < allRecords.length; i++) {
        if (allRecords[i].id == id) {
            r = allRecords[i];
            break;
        }
    }
    
    if (r == null) return;

    var content = document.getElementById('modalContent');
    var actions = document.getElementById('modalActions');
    if (content == null || actions == null) return;

    // 2. Build the HTML table for the popup
    var html = '<div class="lib-table-container"><table class="lib-table w-full">';
    html += '<tr><th>Book</th><td>'   + r.bookTitle + ' (BK-' + r.bookId + ')</td></tr>';
    html += '<tr><th>Member</th><td>' + r.memberName + ' (MEM-' + r.memberId + ')</td></tr>';
    html += '<tr><th>Issued</th><td>' + r.issueDate + '</td></tr>';
    html += '<tr><th>Due Date</th><td>' + r.returnDate + '</td></tr>';
    html += '<tr><th>Status</th><td>' + (r.returned ? 'Returned' : 'Active') + '</td></tr>';
    html += '</table></div>';
    
    content.innerHTML = html;

    // 3. Add the Return button inside the popup if book is active
    if (r.returned == false) {
        actions.innerHTML = '<a href="ReturnBookServlet?id=' + r.id + '" class="lib-btn lib-btn-primary w-full text-center block">Return Book Now</a>';
    } else {
        actions.innerHTML = '<p class="text-success text-center">Book already returned.</p>';
    }

    // 4. Show the modal and the dark background
    document.getElementById('detailModal').style.display = 'block';
    document.getElementById('modalOverlay').style.display = 'block';
}

function closeModal() {
    const modal = document.getElementById('detailModal');
    const overlay = document.getElementById('modalOverlay');
    if (modal) modal.style.display = 'none';
    if (overlay) overlay.style.display = 'none';
}

function showToast(message, isError = false) {
    const existing = document.querySelector('.toast');
    if (existing) existing.remove();

    const toast = document.createElement('div');
    toast.className = 'toast' + (isError ? ' error' : '');
    toast.textContent = message;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(110%)';
        setTimeout(() => toast.remove(), 300);
    }, 2200);
}

// Dynamic Navbar Renderer
// NAVBAR: Renders the navigation links with icons and classes
function renderNavbar(active) {
    var navContainer = document.getElementById('navLinks');
    if (navContainer == null) return;

    var role = localStorage.getItem('userRole');
    var links = "";

    if (role == 'admin') {
        links += '<a href="dashboard.html" class="nav-link"><i class="fas fa-home"></i> Dashboard</a>';
        links += '<a href="booklist.html" class="nav-link"><i class="fas fa-book"></i> Books</a>';
        links += '<a href="memberlist.html" class="nav-link"><i class="fas fa-users"></i> Members</a>';
        links += '<a href="issuedbooklist.html" class="nav-link"><i class="fas fa-exchange-alt"></i> Issued Books</a>';
    } else {
        links += '<a href="userdashboard.html" class="nav-link"><i class="fas fa-home"></i> Home</a>';
        links += '<a href="booklist.html" class="nav-link"><i class="fas fa-book"></i> View Books</a>';
        links += '<a href="issuedbooklist.html" class="nav-link"><i class="fas fa-list"></i> My Books</a>';
    }

    links += '<a href="LogoutServlet" class="lib-btn lib-btn-secondary nav-logout"><i class="fas fa-sign-out-alt"></i> Logout</a>';
    navContainer.innerHTML = links;
}