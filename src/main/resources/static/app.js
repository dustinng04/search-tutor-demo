class TutorSearchApp {
    constructor() {
        this.currentStep = 1;
        this.searchParams = {
            query: null,
            subject: null,
            level: null,
            rating: null,
            availabilities: null
        };
        
        this.days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];
        this.timeSlots = [
            { start: "08:00", end: "10:00" },
            { start: "10:00", end: "12:00" },
            { start: "12:00", end: "14:00" },
            { start: "14:00", end: "16:00" },
            { start: "16:00", end: "18:00" },
            { start: "18:00", end: "20:00" },
            { start: "20:00", end: "22:00" }
        ];
        
        this.init();
    }
    
    init() {
        this.setupEventListeners();
        this.generateAvailabilityGrid();
    }
    
    setupEventListeners() {
        // Subject selection
        document.querySelectorAll('[data-subject]').forEach(card => {
            card.addEventListener('click', (e) => this.selectSubject(e));
        });
        
        // Level selection
        document.querySelectorAll('[data-level]').forEach(card => {
            card.addEventListener('click', (e) => this.selectLevel(e));
        });
        
        // Navigation buttons
        document.getElementById('next-step1').addEventListener('click', () => this.nextStep());
        document.getElementById('next-step2').addEventListener('click', () => this.nextStep());
        document.getElementById('skip-step2').addEventListener('click', () => this.skipStep());
        document.getElementById('skip-step3').addEventListener('click', () => this.skipStep());
        document.getElementById('search-tutors').addEventListener('click', () => this.searchTutors());
        document.getElementById('refined-search-btn').addEventListener('click', () => this.performRefinedSearch());
        document.getElementById('new-search').addEventListener('click', () => this.resetSearch());
    }
    
    selectSubject(e) {
        // Remove selection from all subject cards
        document.querySelectorAll('[data-subject]').forEach(card => {
            card.classList.remove('selected');
        });
        
        // Add selection to clicked card
        e.currentTarget.classList.add('selected');
        this.searchParams.subject = e.currentTarget.dataset.subject;
        
        // Enable continue button
        document.getElementById('next-step1').disabled = false;
    }
    
    selectLevel(e) {
        // Remove selection from all level cards
        document.querySelectorAll('[data-level]').forEach(card => {
            card.classList.remove('selected');
        });
        
        // Add selection to clicked card
        e.currentTarget.classList.add('selected');
        this.searchParams.level = e.currentTarget.dataset.level;
    }
    
    generateAvailabilityGrid() {
        const grid = document.getElementById('availability-grid');
        
        this.days.forEach(day => {
            const dayContainer = document.createElement('div');
            dayContainer.className = 'border border-gray-200 rounded-lg p-4';
            
            const dayHeader = document.createElement('h3');
            dayHeader.className = 'font-semibold text-gray-800 mb-3';
            dayHeader.textContent = day;
            dayContainer.appendChild(dayHeader);
            
            const slotsContainer = document.createElement('div');
            slotsContainer.className = 'grid grid-cols-2 md:grid-cols-4 gap-2';
            
            this.timeSlots.forEach(slot => {
                const slotButton = document.createElement('button');
                slotButton.className = 'time-slot px-3 py-2 text-sm border border-gray-300 rounded-md text-gray-700 hover:bg-gray-100';
                slotButton.textContent = `${slot.start}-${slot.end}`;
                slotButton.dataset.day = day;
                slotButton.dataset.start = slot.start;
                slotButton.dataset.end = slot.end;
                
                slotButton.addEventListener('click', (e) => this.toggleTimeSlot(e));
                
                slotsContainer.appendChild(slotButton);
            });
            
            dayContainer.appendChild(slotsContainer);
            grid.appendChild(dayContainer);
        });
    }
    
    toggleTimeSlot(e) {
        const button = e.currentTarget;
        const day = button.dataset.day;
        const startTime = button.dataset.start;
        const endTime = button.dataset.end;
        
        if (button.classList.contains('selected')) {
            // Remove selection
            button.classList.remove('selected');
            if (this.searchParams.availabilities) {
                this.searchParams.availabilities = this.searchParams.availabilities.filter(
                    avail => !(avail.day === day && avail.startTime === startTime && avail.endTime === endTime)
                );
            }
        } else {
            // Add selection
            button.classList.add('selected');
            if (!this.searchParams.availabilities) {
                this.searchParams.availabilities = [];
            }
            this.searchParams.availabilities.push({
                day: day,
                startTime: startTime,
                endTime: endTime
            });
        }
    }
    
    nextStep() {
        this.currentStep++;
        this.updateUI();
    }
    
    skipStep() {
        if (this.currentStep === 2) {
            this.searchParams.level = null;
            this.currentStep++;
            this.updateUI();
        } else if (this.currentStep === 3) {
            this.searchParams.availabilities = null;
            // Skip directly to search when availability is skipped
            this.searchTutors();
        }
    }
    
    updateUI() {
        // Hide all steps
        document.querySelectorAll('.step-card').forEach(step => {
            step.classList.add('hidden');
            step.classList.remove('active');
        });
        
        // Show current step
        const currentStepElement = document.getElementById(`step${this.currentStep}`);
        if (currentStepElement) {
            currentStepElement.classList.remove('hidden');
            currentStepElement.classList.add('active');
        }
        
        // Update progress indicators
        this.updateProgressIndicators();
        
        // If we've completed all steps, show search
        if (this.currentStep > 3) {
            this.showSearchResults();
        }
    }
    
    updateProgressIndicators() {
        const indicators = ['step1-indicator', 'step2-indicator', 'step3-indicator'];
        const progressBar = document.getElementById('progress-bar');
        
        indicators.forEach((id, index) => {
            const indicator = document.getElementById(id);
            const stepNumber = index + 1;
            
            if (stepNumber < this.currentStep) {
                indicator.className = 'w-8 h-8 rounded-full bg-green-500 text-white flex items-center justify-center text-sm font-semibold';
                indicator.innerHTML = '<i class="fas fa-check"></i>';
            } else if (stepNumber === this.currentStep) {
                indicator.className = 'w-8 h-8 rounded-full bg-blue-500 text-white flex items-center justify-center text-sm font-semibold';
                indicator.textContent = stepNumber;
            } else {
                indicator.className = 'w-8 h-8 rounded-full bg-gray-300 text-gray-600 flex items-center justify-center text-sm font-semibold';
                indicator.textContent = stepNumber;
            }
        });
        
        // Update progress bar width
        const progressPercentage = ((this.currentStep - 1) / 3) * 100;
        progressBar.style.width = `${Math.min(progressPercentage, 100)}%`;
    }
    
    showSearchResults() {
        // Hide all step cards
        document.querySelectorAll('.step-card').forEach(step => {
            step.classList.add('hidden');
        });
        
        // Show search results
        document.getElementById('search-results').classList.remove('hidden');
    }
    
    async searchTutors() {
        this.showSearchResults();
        
        // Show loading
        document.getElementById('loading').classList.remove('hidden');
        document.getElementById('results-container').innerHTML = '';
        
        try {
            // Prepare search parameters
            const params = new URLSearchParams();
            
            if (this.searchParams.query) {
                params.append('query', this.searchParams.query);
            }
            
            if (this.searchParams.subject) {
                params.append('subject', this.searchParams.subject);
            }
            
            if (this.searchParams.level) {
                params.append('level', this.searchParams.level);
            }
            
            if (this.searchParams.rating) {
                params.append('rating', this.searchParams.rating);
            }
            
            params.append('page', '0');
            params.append('size', '10');
            
            // Add availability parameters if any selected
            if (this.searchParams.availabilities && this.searchParams.availabilities.length > 0) {
                this.searchParams.availabilities.forEach((avail, index) => {
                    params.append(`availabilities[${index}].day`, avail.day);
                    params.append(`availabilities[${index}].startTime`, avail.startTime);
                    params.append(`availabilities[${index}].endTime`, avail.endTime);
                });
            }
            
            console.log('Making search request with params:', params.toString());
            const response = await fetch(`/api/search?${params.toString()}`);
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const data = await response.json();
            console.log('Search response:', data);
            this.displayResults(data);
            
        } catch (error) {
            console.error('Search failed:', error);
            this.displayError(error.message);
        } finally {
            document.getElementById('loading').classList.add('hidden');
        }
    }
    
    displayResults(data) {
        const container = document.getElementById('results-container');
        
        if (!data.teachers || data.teachers.length === 0) {
            container.innerHTML = `
                <div class="text-center py-12">
                    <i class="fas fa-search text-4xl text-gray-400 mb-4"></i>
                    <h3 class="text-xl font-semibold text-gray-600 mb-2">No tutors found</h3>
                    <p class="text-gray-500">Try adjusting your search criteria</p>
                </div>
            `;
            return;
        }
        
        const resultsHTML = data.teachers.map(tutor => `
            <div class="border border-gray-200 rounded-lg p-6 mb-4 hover:shadow-md transition-shadow">
                <div class="flex items-start justify-between">
                    <div class="flex-1">
                        <h3 class="text-xl font-semibold text-gray-800 mb-2">${tutor.name || 'Anonymous Tutor'}</h3>
                        <div class="flex items-center mb-2">
                            <span class="bg-blue-100 text-blue-800 text-sm font-medium px-3 py-1 rounded-full mr-2">
                                ${tutor.subject || 'Subject not specified'}
                            </span>
                            ${tutor.level ? `<span class="bg-green-100 text-green-800 text-sm font-medium px-3 py-1 rounded-full mr-2">${tutor.level}</span>` : ''}
                        </div>
                        ${tutor.description ? `<p class="text-gray-600 mb-3">${tutor.description}</p>` : ''}
                        ${tutor.availabilities && tutor.availabilities.length > 0 ? `
                            <div class="mb-3">
                                <h4 class="text-sm font-semibold text-gray-700 mb-2">Available Times:</h4>
                                <div class="flex flex-wrap gap-2">
                                    ${tutor.availabilities.map(avail => `
                                        <span class="bg-gray-100 text-gray-700 text-xs px-2 py-1 rounded">
                                            ${avail.day} ${this.formatTime(avail.start)}-${this.formatTime(avail.end)}
                                        </span>
                                    `).join('')}
                                </div>
                            </div>
                        ` : ''}
                    </div>
                    <div class="text-right ml-4">
                        ${tutor.rating ? `
                            <div class="flex items-center mb-2">
                                <i class="fas fa-star text-yellow-400 mr-1"></i>
                                <span class="font-semibold text-gray-800">${tutor.rating}</span>
                            </div>
                        ` : ''}
                        <button class="bg-blue-500 text-white px-4 py-2 rounded-lg font-medium hover:bg-blue-600 transition-colors">
                            Contact Tutor
                        </button>
                    </div>
                </div>
            </div>
        `).join('');
        
        const paginationInfo = data.pagination ? `
            <div class="text-center mt-6 text-gray-600">
                <p>Showing ${data.pagination.numberOfElements} of ${data.pagination.totalElements} results</p>
                <p class="text-sm mt-1">Search completed in ${data.timeTaken}ms</p>
            </div>
        ` : '';
        
        container.innerHTML = resultsHTML + paginationInfo;
    }
    
    displayError(errorMessage) {
        const container = document.getElementById('results-container');
        container.innerHTML = `
            <div class="text-center py-12">
                <i class="fas fa-exclamation-triangle text-4xl text-red-400 mb-4"></i>
                <h3 class="text-xl font-semibold text-red-600 mb-2">Search Error</h3>
                <p class="text-gray-600">${errorMessage}</p>
                <button onclick="location.reload()" class="mt-4 bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 transition-colors">
                    Try Again
                </button>
            </div>
        `;
    }
    
    formatTime(timeStr) {
        if (typeof timeStr === 'string') {
            return timeStr; // Already formatted
        }
        if (typeof timeStr === 'object' && timeStr.hour !== undefined) {
            // LocalTime object format
            const hour = timeStr.hour.toString().padStart(2, '0');
            const minute = (timeStr.minute || 0).toString().padStart(2, '0');
            return `${hour}:${minute}`;
        }
        return timeStr; // Fallback
    }
    
    performRefinedSearch() {
        // Get values from the refinement form
        const queryInput = document.getElementById('search-query').value.trim();
        const ratingSelect = document.getElementById('rating-select').value;
        
        // Update search params with refined values
        this.searchParams.query = queryInput || null;
        this.searchParams.rating = ratingSelect ? parseFloat(ratingSelect) : null;
        
        // Perform the search
        this.searchTutors();
    }
    
    resetSearch() {
        this.currentStep = 1;
        this.searchParams = {
            query: null,
            subject: null,
            level: null,
            rating: null,
            availabilities: null
        };
        
        // Reset UI
        document.querySelectorAll('.option-card').forEach(card => {
            card.classList.remove('selected');
        });
        
        document.querySelectorAll('.time-slot').forEach(slot => {
            slot.classList.remove('selected');
        });
        
        document.getElementById('next-step1').disabled = true;
        document.getElementById('search-results').classList.add('hidden');
        
        // Reset refinement form
        const searchQueryInput = document.getElementById('search-query');
        const ratingSelect = document.getElementById('rating-select');
        if (searchQueryInput) searchQueryInput.value = '';
        if (ratingSelect) ratingSelect.value = '';
        
        this.updateUI();
    }
}

// Initialize the app when the DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new TutorSearchApp();
});
