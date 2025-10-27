import {Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend} from 'chart.js';
import { Bar } from 'react-chartjs-2';
import '../styles/GraphsStyle.css'

ChartJS.register( CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

export function MonthlyExpenses({ expenses }) {
    const getCurrentMonthData = () => {
        const currentDate = new Date();
        const currentMonth = currentDate.getMonth();
        const currentYear = currentDate.getFullYear();
        const categoryData = {};
        let totalAmount = 0;

        expenses.forEach(expense => {
            const date = new Date(expense.date);

            if(date.getMonth() === currentMonth && date.getFullYear() === currentYear) {
                const category = expense.category || 'Others';
                if (!categoryData[category]) {
                    categoryData[category] = 0;
                }
                categoryData[category] += expense.amount;
                totalAmount += expense.amount;
            }
        });
        return { categoryData, totalAmount };
    }
    const monthData = getCurrentMonthData();
    const categories = Object.keys(monthData.categoryData);
    const amounts = Object.values(monthData.categoryData);
    const sortedData = categories.map((category, index) => ({
        category,
        amount: amounts[index]
    })).sort((a, b) => b.amount - a.amount);

    //Get sorted categories and amounts separately
    const sortedCategories = sortedData.map(data => data.category);
    const sortedAmounts = sortedData.map(data => data.amount);

    const backgroundColors = [
    'rgba(255, 99, 132, 0.8)',    
    'rgba(54, 162, 235, 0.8)',    
    'rgba(255, 206, 86, 0.8)',   
    'rgba(75, 192, 192, 0.8)',    
    ];
    const borderColors = backgroundColors.map(color => color.replace('0.8', '1'));

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
    };

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
            const value = context.parsed.y;
            const total = monthData.total;
            const percentage = total > 0 ? ((value / total) * 100).toFixed(1) : 0;
            return `€${value.toFixed(2)} (${percentage}% of total)`;
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
            return '€' + value;
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
    };

    const currentDate = new Date();
    const currentMonthName = currentDate.toLocaleString('default', { 
        month: 'long' 
    });
    const currentYear = currentDate.getFullYear();

    return (
    <div className="chart-card bar-chart-container">
        <h4 className="chart-title">
        Expenses for {currentMonthName} {currentYear}
            <div className='chart-subtitle'>
            Total: €{monthData.totalAmount.toFixed(2)}
            </div>
        </h4>
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
    );

}
