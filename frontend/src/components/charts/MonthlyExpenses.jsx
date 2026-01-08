import {Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend} from 'chart.js'
import { Bar } from 'react-chartjs-2'
import '../../styles/GraphsStyle.css'
import {useState} from 'react'

ChartJS.register( CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend)

export function MonthlyExpenses({ expenses }) {
    const [selectedMonth, setSelectedMonth] = useState(new Date().getMonth())
    const [selectedYear, setSelectedYear] = useState(new Date().getFullYear())
    const months = [
        'January', 'February', 'March', 'April', 'May', 'June',
        'July', 'August', 'September', 'October', 'November', 'December'
    ]

    const currentYear = new Date().getFullYear()
    const years = Array.from({ length: 8 }, (_, i) => currentYear - 5 + i)

    const getCurrentMonthData = (month = selectedMonth, year = selectedYear) => {
        const categoryData = {}
        let totalAmount = 0

        expenses.forEach(expense => {
            const date = new Date(expense.date)

            if(date.getMonth() === month && date.getFullYear() === year) {
                const category = expense.category || 'Others'
                if (!categoryData[category]) {
                    categoryData[category] = 0
                }
                categoryData[category] += expense.amount
                totalAmount += expense.amount
            }
        })
        return { categoryData, totalAmount }
    }
    const handleMonthChange = (e) => {
        setSelectedMonth(parseInt(e.target.value))
    }

    const handleYearChange = (e) => {
        setSelectedYear(parseInt(e.target.value))
    }
    const monthData = getCurrentMonthData()
    const categories = Object.keys(monthData.categoryData)
    const amounts = Object.values(monthData.categoryData)
    const sortedData = categories.map((category, index) => ({
        category,
        amount: amounts[index]
    })).sort((a, b) => b.amount - a.amount)

    //Get sorted categories and amounts separately
    const sortedCategories = sortedData.map(data => data.category)
    const sortedAmounts = sortedData.map(data => data.amount)

    const backgroundColors = [
    'rgba(255, 99, 132, 0.8)',    
    'rgba(54, 162, 235, 0.8)',    
    'rgba(255, 206, 86, 0.8)',   
    'rgba(75, 192, 192, 0.8)',    
    ]
    const borderColors = backgroundColors.map(color => color.replace('0.8', '1'))

    const chartData = {
    labels: sortedCategories,
    datasets: [
        {
        label: 'Expenses',
        data: sortedAmounts,
        backgroundColor: backgroundColors,
        borderColor: borderColors,
        borderWidth:  1 ,
        borderRadius: 4,
        hoverBackgroundColor: 'rgba(229, 115, 115, 0.9)',
        },
    ],
    }

    const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
        legend: {
        display: false, 
        },
        tooltip: {
        callbacks: {
            label: function(context) {
            const value = context.parsed.y
            const total = monthData.total
            const percentage = total > 0 ? ((value / total) * 100).toFixed(1) : 0
            return `€${value.toFixed(2)} (${percentage}% of total)`
            }
        },
        },
    },
    scales: {
        y: {
        beginAtZero: true,
        ticks: {
            color: '#e0e0e0',
            font: {
            size:11 ,
            },
            callback: function(value) {
            return '€' + value
            }
        },
        title: {
            text: 'Amount (€)',
            color: 'white',
        },
        grid: {
            color: 'rgba(255,255,255,0.1)' ,
        }
        },
        x: {
        ticks: {
            color: '#e0e0e0',
            font: {
            size: 12 ,
            },
        },
        title: {

            text: 'Categorías',
            color: 'white',
        },
        grid: {
            display: false, 
        }
        }
    },
    animation: {
        duration:  500 ,
    }
    }

    const currentDate = new Date()
    const currentMonthName = currentDate.toLocaleString('default', { 
        month: 'long' 
    })
    const selectedMonthName = months[selectedMonth]

    return (
    <div className="chart-card bar-chart-container">
        <h4 className="chart-title">
        Expenses for {selectedMonthName} {selectedYear}
            <div className='chart-subtitle'>
            Total: €{monthData.totalAmount.toFixed(2)}
            </div>
        </h4>
        <div className='date-selector'>
            <select 
                value={selectedMonth} 
                onChange={handleMonthChange}
                className="month-select">

                {months.map((month, index) => (
                    <option key={month} value={index}>
                        {month}
                    </option>
                ))}
            </select>
            <select 
                value={selectedYear} 
                onChange={handleYearChange}
                className="year-select">

                {years.map(year => (
                    <option key={year} value={year}>
                        {year}
                    </option>
                ))}
            </select>
        </div>
        <div className='chart-content'>
            {categories.length > 0 ? (
            <Bar data={chartData} options={options} />
            ) : (
                <div className='chart-no-data'>
                <p>No expenese to show</p>
                <p >to {currentMonthName} {currentYear}</p>
                </div>
            )}
        </div>
    </div>
    )

}
