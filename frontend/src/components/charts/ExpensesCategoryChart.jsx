import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import { Pie } from 'react-chartjs-2';

ChartJS.register(ArcElement, Tooltip, Legend);

export function ExpenseCategoryChart({ expenses}) {
    const getCategoryData = () => {
    const categoryData = {};
    let totalAmount = 0;
    
    expenses.forEach(expense => {
        const category = expense.category || 'Others';
        if (!categoryData[category]) {
        categoryData[category] = 0;
        }
        categoryData[category] += expense.amount;
        totalAmount += expense.amount;
    });
    
    return { categoryData: categoryData, totalAmount: totalAmount};
    };

    const categoryData = getCategoryData();
    const categories = Object.keys(categoryData.categoryData);
    const amounts = Object.values(categoryData.categoryData);


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
        borderWidth:  1 ,
        hoverOffset: 15, 
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
        position:  'bottom' ,
        labels: {
            color: 'white',
            font: {
            size: 12,
            },
            boxWidth: 12 ,
            padding: 10 ,
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
        },
    },
    };
    return (
    <div className="chart-card pie-chart-container">
        <h4 className="chart-title">
        Expenses by Category
            <div className="chart-subtitle">
                Total: €{categoryData.totalAmount.toFixed(2)}
            </div>
        </h4>
        <div className="chart-content">
            {categories.length > 0 ? (
            <Pie data={chartData} options={options} />
            ) : (
            <p className='chart-no-data'>
                No expenses to show
            </p>
            )}
        </div>
    </div>
    );
}