import {Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend} from 'chart.js';
import { Bar } from 'react-chartjs-2';

ChartJS.register( CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

export function MonthlyExpenses({ expenses,size = 'small'  }) {
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
        borderWidth: size === 'small' ? 1 : 2,
        borderRadius: 4,
        hoverBackgroundColor: 'rgba(229, 115, 115, 0.9)',
        },
    ],
    };

    const options = {
    responsive: true,
    maintainAspectRatio: false,
    layout:{
        padding: {
            top: 10,    
            bottom: 15,  
            left: 10,
            right: 10
        }
    },
    plugins: {
        legend: {
        display: false, 
        },
        title: {
        display: size !== 'small',
        text: `Expenses actual month - Total: €${monthData.totalAmount.toFixed(2)}`,
        color: 'white',
        font: {
            size: size === 'small' ? 12 : 14,
        }
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
        bodyFont: {
            size: size === 'small' ? 11 : 12,
        },
        },
    },
    scales: {
        y: {
        beginAtZero: true,
        ticks: {
            color: '#e0e0e0',
            font: {
            size: size === 'small' ? 10 : 12,
            },
            callback: function(value) {
            return '€' + value;
            }
        },
        title: {
            display: size !== 'small',
            text: 'Amount (€)',
            color: 'white',
        },
        grid: {
            color: size === 'small' ? 'rgba(255,255,255,0.1)' : 'rgba(255,255,255,0.2)',
        }
        },
        x: {
        ticks: {
            color: '#e0e0e0',
            font: {
            size: size === 'small' ? 10 : 12,
            },
        },
        title: {
            display: size !== 'small',
            text: 'Categorías',
            color: 'white',
        },
        grid: {
            display: false, 
        }
        }
    },
    animation: {
        duration: size === 'small' ? 500 : 1000,
    }
    };

    const containerStyle = {
    margin: size === 'small' ? '10px 0' : '20px 0',
    padding: size === 'small' ? '12px' : '20px',
    backgroundColor: '#16213e',
    borderRadius: '8px',
    height: size === 'small' ? '250px' : '400px',
    minHeight: size === 'small' ? '250px' : '400px',
    };

    const currentDate = new Date();
    const currentMonthName = currentDate.toLocaleString('default', { 
        month: 'long' 
    });
    const currentYear = currentDate.getFullYear();

    return (
    <div style={containerStyle}>
        <h4 style={{ 
        color: 'white', 
        textAlign: 'center', 
        margin: '0 0 15px 0', 
        fontSize: size === 'small' ? '0.9rem' : '1.1rem' 
        }}>
        Expenses for {currentMonthName} {currentYear}
        {size === 'small' && (
            <div style={{ fontSize: '0.8rem', opacity: 0.8, marginTop: '5px' }}>
            Total: €{monthData.totalAmount.toFixed(2)}
            </div>
        )}
        </h4>

        {categories.length > 0 ? (
        <Bar data={chartData} options={options} />
        ) : (
        <div style={{ 
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'center', 
            height: '100%',
            color: 'white',
            textAlign: 'center'
        }}>
            <div>
            <p>No expenese to show</p>
            <p style={{ fontSize: '0.8rem', opacity: 0.7 }}>to {currentMonthName} {currentYear}</p>
            </div>
        </div>
        )}
    </div>
    );

}
