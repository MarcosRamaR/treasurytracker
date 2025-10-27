import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import { Pie } from 'react-chartjs-2';

ChartJS.register(ArcElement, Tooltip, Legend);

export function ExpenseCategoryChart({ expenses, size = 'large' }) {
    const getCategoryData = () => {
    const categoryData = {};
    
    expenses.forEach(expense => {
        const category = expense.category || 'Others';
        if (!categoryData[category]) {
        categoryData[category] = 0;
        }
        categoryData[category] += expense.amount;
    });
    
    return categoryData;
    };

    const categoryData = getCategoryData();
    const categories = Object.keys(categoryData);
    const amounts = Object.values(categoryData);


    const backgroundColors = [
    'rgba(255, 99, 132, 0.8)',    
    'rgba(54, 162, 235, 0.8)',    
    'rgba(255, 206, 86, 0.8)',   
    'rgba(75, 192, 192, 0.8)',
    'rgba(153, 102, 255, 0.8)',
    'rgba(255, 159, 64, 0.8)',  
    'rgba(199, 199, 199, 0.8)',   
    'rgba(83, 102, 255, 0.8)',     
    ];

    const borderColors = backgroundColors.map(color => color.replace('0.8', '1'));

    const chartData = {
    labels: categories,
    datasets: [
        {
        data: amounts, //Import the amounts calculated for category
        backgroundColor: backgroundColors.slice(0, categories.length), //So many colors as categories
        borderColor: borderColors.slice(0, categories.length),
        borderWidth: size === 'small' ? 1 : 2,
        hoverOffset: 15, 
        },
    ],
    };

    const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
        legend: {
        position: size === 'small' ? 'bottom' : 'right',
        labels: {
            color: 'white',
            font: {
            size: size === 'small' ? 10 : 12,
            },
            boxWidth: size === 'small' ? 12 : 15,
            padding: size === 'small' ? 10 : 15,
        },
        },
        tooltip: {
        callbacks: { //Show this information in tooltip when hovering
            label: function(context) {
            const label = context.label || '';
            const value = context.parsed;
            const total = context.dataset.data.reduce((a, b) => a + b, 0);
            const percentage = ((value / total) * 100).toFixed(1);
            return `${label}: €${value.toFixed(2)} (${percentage}%)`;
            }
        },
        bodyFont: {
            size: size === 'small' ? 11 : 12,
        },
        },
    },
    };

    const containerStyle = {
    margin: size === 'small' ? '10px 0' : '20px 0',
    padding: size === 'small' ? '12px' : '20px',
    backgroundColor: '#16213e',
    borderRadius: '8px',
    height: size === 'small' ? '250px' : '400px',
    minHeight: size === 'small' ? '250px' : '400px',
    };

    return (
    <div style={containerStyle}>
        <h4 style={{ color: 'white', textAlign: 'center', margin: '0 0 15px 0', fontSize: size === 'small' ? '0.9rem' : '1.1rem' }}>
        Expenses by Category
        </h4>
        {categories.length > 0 ? (
        <Pie data={chartData} options={options} />
        ) : (
        <p style={{ color: 'white', textAlign: 'center', margin: '20px 0' }}>
            No expenses to show
        </p>
        )}
    </div>
    );
}