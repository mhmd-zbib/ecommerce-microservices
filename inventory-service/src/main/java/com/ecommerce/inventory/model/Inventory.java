@Entity
@Table(name = "inventory")
@Data
@Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String skuCode;
    
    private Integer quantity;
    
    @Version
    private Long version;
} 